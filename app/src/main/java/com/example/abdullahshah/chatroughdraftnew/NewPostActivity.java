package com.example.abdullahshah.chatroughdraftnew;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.abdullahshah.chatroughdraftnew.Fragments.FeedFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.abdullahshah.chatroughdraftnew.R.drawable.ic_add_white_24dp;
import static com.example.abdullahshah.chatroughdraftnew.R.drawable.ic_remove_circle_outline_white_24dp;

public class NewPostActivity extends AppCompatActivity {

    private EditText mTitleField;
    private EditText mBodyField;
    private ImageView mImageField;
    private FloatingActionButton mSubmitButton;
    private FloatingActionButton mAddImage;
    private final static int GALLERY_PICK = 1;

    private FirebaseUser mAuth;
    private DatabaseReference mCommunityDatabase;
    private DatabaseReference mUsersDatabase;
    private StorageReference mCommunityImages;

    private String mCurrentUser_name = "";
    private String feedName = "";
    private Uri imageUri;

    private boolean imageAdded;
    private int buttonstatus = 0;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mTitleField = findViewById(R.id.newpost_title);
        mBodyField = findViewById(R.id.newpost_description);
        mImageField = findViewById(R.id.newpost_image);
        mImageField.setVisibility(View.GONE);
        mSubmitButton = findViewById(R.id.fab_submit_post);
        mAddImage = findViewById(R.id.fab_add_image);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Uploading Post");
        mProgressDialog.setMessage("Process may be slow depending on your connection");
        mProgressDialog.setCanceledOnTouchOutside(false);


        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        final String mCurrentuser = mAuth.getUid();
        mCommunityDatabase = FirebaseDatabase.getInstance().getReference().child("Communities");
        mCommunityImages = FirebaseStorage.getInstance().getReference().child("Feed_Images");

        feedName = getIntent().getStringExtra("Community_Name");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentuser);
        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser_name = dataSnapshot.child("first_name").getValue().toString() +
                        " " + dataSnapshot.child("last_name").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonstatus == 0) {
                    mImageField.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent galleryIntent = new Intent();
                            galleryIntent.setType("image/*");
                            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                            imageAdded = true;
                        }
                    });
                    mImageField.setVisibility(View.VISIBLE);
                    mAddImage.setImageResource(ic_remove_circle_outline_white_24dp);
                } else {
                    Picasso.get().cancelRequest(mImageField);
                    mAddImage.setImageResource(ic_add_white_24dp);
                    mImageField.setVisibility(View.GONE);
                    buttonstatus = 0;
                    imageAdded = false;
                }
            }
        });



        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateSubmission(mTitleField, mBodyField);
                //Toast.makeText(getApplicationContext(), "good to go", Toast.LENGTH_SHORT).show();
                mProgressDialog.show();
                mSubmitButton.setClickable(false);
                DatabaseReference post_message_push = mCommunityDatabase.child(feedName).push();
                String push_id = post_message_push.getKey();


                Map postMap = new HashMap();
                postMap.put("uID", mCurrentuser);
                postMap.put("author", mCurrentUser_name);
                postMap.put("title", mTitleField.getText().toString());
                postMap.put("description", mBodyField.getText().toString());
                postMap.put("time", DateFormat.getDateTimeInstance().format(new Date()));

                if(imageAdded){
                    uploadImage(imageUri, push_id);
                } else {
                    postMap.put("image", null);
                }

                post_message_push.updateChildren(postMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d("POST_UPLOAD_LOG", databaseError.getMessage().toString());
                        }
                    }
                });

                mProgressDialog.dismiss();
                Intent gotoFeed = new Intent(NewPostActivity.this, MainActivity.class);
                startActivity(gotoFeed);
                finish();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            imageUri = data.getData();

            Picasso.get().load(imageUri).into(mImageField, new Callback() {
                @Override
                public void onSuccess() {
                    mAddImage.setImageResource(ic_remove_circle_outline_white_24dp);
                    buttonstatus = 1;
                }
                @Override
                public void onError(Exception e) {

                }
            });
        }
    }

    private void uploadImage(Uri uri, final String pushid){
        final String[] feedimagepath = new String[1];

        StorageReference filepath = mCommunityImages.child(feedName).child(pushid).child("post_image.jpg");
        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    feedimagepath[0] = task.getResult().getDownloadUrl().toString();
                    DatabaseReference feedImagepush = FirebaseDatabase.getInstance().getReference().child("Communities")
                            .child(feedName).child(pushid).child("image");
                    //Toast.makeText(getApplicationContext(), feedimagepath[0], Toast.LENGTH_SHORT).show();
                    feedImagepush.setValue(feedimagepath[0]);
                } else {
                    Log.i("ERROR", task.getException().toString());
                    Toast.makeText(getApplicationContext(), "Error in uploading image", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void validateSubmission(EditText mTitleField, EditText mBodyField) {
        mTitleField.setError(null);
        mTitleField.setError(null);
        // Store values at the time of the login attempt.

        String title = mTitleField.getText().toString();
        String body = mBodyField.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check if username is given
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(getString(R.string.error_field_required));
            focusView = mTitleField;
            cancel = true;
        }
        // Check if namebox is given
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(getString(R.string.error_field_required));
            focusView = mBodyField;
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
