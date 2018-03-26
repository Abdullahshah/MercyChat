package com.example.abdullahshah.chatroughdraftnew.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdullahshah.chatroughdraftnew.MainActivity;
import com.example.abdullahshah.chatroughdraftnew.R;
import com.example.abdullahshah.chatroughdraftnew.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * @link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the @link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 **/
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;


    private CircleImageView mProfileImage;
    private TextView mFirstnamelastname;
    private TextView mFollowers;
    private TextView mFollowing;
    private TextView mUsername;
    private TextView mEmail;
    private TextView mStatus;

    private TextView mEditProfile;
    Dialog editDialog;

    private Button logoutbtn;

    private ProgressDialog mProgressDialog;

    ImageView newProfileImage;
    String profileImagepath;

    public static final int GALLERY_PICK = 1;

    //private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


        mProfileImage = rootView.findViewById(R.id.profile_image);
        mFirstnamelastname = rootView.findViewById(R.id.nameBox);
        mFollowers = rootView.findViewById(R.id.followers_number);
        mFollowing = rootView.findViewById(R.id.following_number);
        mUsername = rootView.findViewById(R.id.profile_username);
        mEmail = rootView.findViewById(R.id.profile_email);
        mStatus = rootView.findViewById(R.id.profile_status);
        logoutbtn = rootView.findViewById(R.id.logoutbtn);

        mEditProfile = rootView.findViewById(R.id.edit_profile);
        editDialog = new Dialog(getContext());

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uID = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uID);
        mDatabase.keepSynced(true);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("first_name").getValue().toString() +
                        " " + dataSnapshot.child("last_name").getValue().toString();
                String username = dataSnapshot.child("username").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                profileImagepath = dataSnapshot.child("image").getValue().toString();
                String thumbImagepath = dataSnapshot.child("thumb_image").getValue().toString();
                String followers = dataSnapshot.child("followers").getValue().toString();
                String following = dataSnapshot.child("following").getValue().toString();

                if(!profileImagepath.equals("default")) {

                    //Picasso.get().load(profileImagepath).placeholder(R.drawable.default_profile).into(mProfileImage);
                    Picasso.get().load(profileImagepath).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_profile).into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(profileImagepath).placeholder(R.drawable.default_profile).into(mProfileImage);
                        }
                    });




                }

                mFirstnamelastname.setText(name);
                mUsername.setText(username);
                mEmail.setText(email);
                mStatus.setText(status);
                mFollowers.setText(followers);
                mFollowing.setText(following);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mImageStorage = FirebaseStorage.getInstance().getReference();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                File thumb_filePath = new File(resultUri.getPath());


                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setTitle("Setting Image");
                mProgressDialog.setMessage("Process may be slow depending on your connection");
                mProgressDialog.setCanceledOnTouchOutside(false);

                String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();


                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(getContext())
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte_data = baos.toByteArray();



                StorageReference filepath = mImageStorage.child("profile_images").child(uID).child("profile_image.jpg");
                final StorageReference thumbimage_filepath = mImageStorage.child("profile_images").child(uID).child("thumb_image.jpg");

                mProgressDialog.show();

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumbimage_filepath.putBytes(thumb_byte_data);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap<>();
                                        update_hashMap.put("image", download_url);
                                        update_hashMap.put("thumb_image", thumb_downloadUrl);

                                        mDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    mProgressDialog.dismiss();
                                                    Picasso.get().load(profileImagepath).into(newProfileImage);

                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(getContext(), "Error Uploading Thumbnail", Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });

                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Log.e("Error:", result.getError().toString());
                mProgressDialog.dismiss();

            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("online").setValue("false");
                mAuth.signOut();
                Intent intent = new Intent(getContext(), StartActivity.class);
                startActivity(intent);
            }
        });

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Offer choice to change color of profile
                TextView txtclose;

                final EditText newName;
                final EditText newUsername;
                final TextView newEmail;
                final EditText newStatus;
                Button saveEditbutn;

                editDialog.setContentView(R.layout.profilepopup);
                txtclose = editDialog.findViewById(R.id.profileEdit_exit);
                newProfileImage = editDialog.findViewById(R.id.profile_imagepopup);
                newName = editDialog.findViewById(R.id.nameBox_profilepopup);
                newUsername = editDialog.findViewById(R.id.newUsername_profilEdit);
                newEmail = editDialog.findViewById(R.id.newEmail_profileEdit);
                newStatus = editDialog.findViewById(R.id.newStatus_profileEdit);
                saveEditbutn = editDialog.findViewById(R.id.profileEdit_save);

                newName.setText(mFirstnamelastname.getText());
                newUsername.setText(mUsername.getText());
                newEmail.setText(mEmail.getText());
                newStatus.setText(mStatus.getText());

                if(profileImagepath.equals("default")){
                    newProfileImage.setImageResource(R.drawable.default_profile);
                } else {
                    Picasso.get().load(profileImagepath).into(newProfileImage);
                }

                newProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    /*
                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"),GALLERY_PICK );
                    */
                        try {
                            CropImage.activity()
                                    .setAspectRatio(1,1)
                                    .start(getContext(), ProfileFragment.this);
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                });



                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setTitle("Updating Profile");
                mProgressDialog.setMessage("Process may be slow depending on your connection");
                mProgressDialog.setCanceledOnTouchOutside(false);

                saveEditbutn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        validateProfileEdits(newName, newUsername);
                        mProgressDialog.show();
                        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference mNewDatabase = FirebaseDatabase.getInstance()
                                .getReference().child("Users").child(uID);
                        String mNewUsername = newUsername.getText().toString();
                        mNewDatabase.child("username").setValue(mNewUsername);
                        mNewDatabase.child("status").setValue(newStatus.getText().toString());
                        try {
                            String mNewName[] = newName.getText().toString().split("\\s+");
                            mNewDatabase.child("first_name").setValue(mNewName[0]);
                            mNewDatabase.child("last_name").setValue(mNewName[1]);
                        } catch (Exception e){
                            e.printStackTrace();
                            mNewDatabase.child("first_name").setValue(newName.getText().toString());
                            mNewDatabase.child("last_name").setValue("");
                        }
                        editDialog.dismiss();
                        mProgressDialog.dismiss();
                    }
                });

                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editDialog.dismiss();
                    }
                });
                editDialog.show();
            }
        });


    }
    private void validateProfileEdits(EditText mNameBox, EditText mUsername) {
        mNameBox.setError(null);
        mUsername.setError(null);
        // Store values at the time of the login attempt.

        String name = mNameBox.getText().toString();
        String username = mUsername.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check if username is given
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        }
        // Check if namebox is given
        if (TextUtils.isEmpty(name)) {
            mNameBox.setError(getString(R.string.error_field_required));
            focusView = mNameBox;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
        }
    }
}


