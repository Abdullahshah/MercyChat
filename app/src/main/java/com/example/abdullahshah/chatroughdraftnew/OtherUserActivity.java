package com.example.abdullahshah.chatroughdraftnew;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdullahshah.chatroughdraftnew.Fragments.chatsubFragments.ChatsSubFragment;
import com.example.abdullahshah.chatroughdraftnew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OtherUserActivity extends AppCompatActivity {



    private ImageView mOUserProfileImage;
    private TextView mOUsernamebox;
    private TextView mOUserfollowers;
    private TextView mOUserfollowing;
    private TextView mOUsername;
    private TextView mOUseremail;
    private TextView mOUserstatus;
    private TextView mAddfriend;
    private TextView mStartChat;

    private DatabaseReference mRootref;
    private DatabaseReference mOUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;

    private String mCurrent_state;

    private ImageView addFriendimage;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);

        userID = getIntent().getStringExtra("friendID");
        Toast.makeText(getApplicationContext(), "FriendID: " + userID, Toast.LENGTH_SHORT).show();

        mOUserProfileImage = findViewById(R.id.otheruser_profileimage);
        mOUsernamebox = findViewById(R.id.otheruser_namebox);
        mOUserfollowers = findViewById(R.id.otheruser_followers);
        mOUserfollowing = findViewById(R.id.otheruser_following);
        mOUsername = findViewById(R.id.otheruser_username);
        mOUseremail = findViewById(R.id.otheruser_email);
        mOUserstatus = findViewById(R.id.otheruser_status);
        mAddfriend = findViewById(R.id.otheruser_addfriend);
        mStartChat = findViewById(R.id.otheruser_startchat);

        addFriendimage = findViewById(R.id.addfriendimage);

        mCurrent_state = "not_friends";

        /*
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Process may be slow depending on your connection");
        mProgressDialog.setCanceledOnTouchOutside(false);
        */
        mRootref = FirebaseDatabase.getInstance().getReference();
        mOUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(getApplicationContext(), "me: " + mCurrent_user.getUid(), Toast.LENGTH_LONG).show();


        mOUsersDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profileImagepath = dataSnapshot.child("image").getValue().toString();
                mOUsernamebox.setText(dataSnapshot.child("first_name").getValue().toString() +
                        " " + dataSnapshot.child("last_name").getValue().toString());
                mOUserfollowers.setText(dataSnapshot.child("followers").getValue().toString());
                mOUserfollowing.setText(dataSnapshot.child("following").getValue().toString());
                mOUsername.setText(dataSnapshot.child("username").getValue().toString());
                mOUseremail.setText(dataSnapshot.child("email").getValue().toString());
                mOUserstatus.setText(dataSnapshot.child("status").getValue().toString());

                if (!profileImagepath.equals("default")) {
                    Picasso.get().load(profileImagepath).placeholder(R.drawable.default_profile).into(mOUserProfileImage);
                }

                // ---- FRIENDS LIST / REQUEST FEATURE

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userID)){
                            String req_type = dataSnapshot.child(userID).child("request_type").getValue().toString();

                            if(req_type.equals("received")){
                                mCurrent_state = "req_received";
                                mAddfriend.setText("Accept Friend Request");
                                addFriendimage.setImageResource(R.drawable.ic_add_startblue_24dp);
                                //Toast.makeText(OtherUserActivity.this, "Friend Request Accepted", Toast.LENGTH_SHORT).show();
                            } else if(req_type.equals("sent")){
                                mCurrent_state = "req_sent";
                                mAddfriend.setText("Cancel Friend Request");
                                addFriendimage.setImageResource(R.drawable.ic_cancel_startblue_24dp);
                            }  else {
                                mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(userID)){
                                            mCurrent_state = "friends";
                                            mAddfriend.setText("Unfriend " + mOUsernamebox.getText().toString());
                                            addFriendimage.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(userID)){
                            mCurrent_state = "friends";
                            mAddfriend.setText("Unfriend " + mOUsernamebox.getText().toString());
                            addFriendimage.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mAddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mAddfriend.setEnabled(false);

                // ---- SEND REQUEST STATE

                if(mCurrent_state.equals("not_friends")){
                    DatabaseReference newNotificationref = mRootref.child("Notifications").child(userID).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrent_user.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + userID + "/request_type", "sent");
                    requestMap.put("Friend_req/" + userID + "/" + mCurrent_user.getUid() + "/request_type", "received");
                    requestMap.put("Notifications/" + userID + "/" + newNotificationId, notificationData);

                    mRootref.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Toast.makeText(getApplicationContext(), "Error In Sending Friend Request", Toast.LENGTH_LONG).show();
                            }
                            mCurrent_state = "req_sent";
                            mAddfriend.setText("Cancel Friend Request");
                            addFriendimage.setImageResource(R.drawable.ic_cancel_startblue_24dp);
                            Toast.makeText(OtherUserActivity.this, "Friend Request Sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // ---- CANCEL REQUEST STATE
                if(mCurrent_state.equals("req_sent")){
                    Map cancelrequestMap = new HashMap();

                    cancelrequestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + userID, null);
                    cancelrequestMap.put("Friend_req/" + userID + "/" + mCurrent_user.getUid(), null);

                    mRootref.updateChildren(cancelrequestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                mCurrent_state = "not_friends";
                                mAddfriend.setText("Add friend");
                                addFriendimage.setImageResource(R.drawable.ic_group_add_startblue_24dp);
                                Toast.makeText(OtherUserActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("CANCELING FRIEND REQUEST ERROR", databaseError.getMessage());
                                Toast.makeText(getApplicationContext(), "Error In Cancelling Friend Request", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }


                // ---- REQUEST RECEIVED STATE

                if(mCurrent_state.equals("req_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + userID + "/date", currentDate);
                    friendsMap.put("Friends/" + userID + "/" + mCurrent_user.getUid() + "/date", currentDate);

                    friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + userID, null);
                    friendsMap.put("Friend_req/" + userID + "/" + mCurrent_user.getUid(), null);


                    mRootref.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                mCurrent_state = "friends";
                                mAddfriend.setText("Unfriend " + mOUsernamebox.getText().toString());
                                addFriendimage.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp);
                                Toast.makeText(OtherUserActivity.this, "Friend Added", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("ADDING FRIEND ERROR", databaseError.getMessage());
                                Toast.makeText(getApplicationContext(), "Error In Accepting Friend Request", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

                if(mCurrent_state.equals("friends")){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + userID, null);
                    unfriendMap.put("Friends/" + userID + "/" + mCurrent_user.getUid(), null);

                    mRootref.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null){
                                mCurrent_state = "not_friends";
                                mAddfriend.setText("Add friend");
                                addFriendimage.setImageResource(R.drawable.ic_group_add_startblue_24dp);
                                Toast.makeText(OtherUserActivity.this, "Friend Removed", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("REMOVING FRIEND ERROR", databaseError.getMessage());
                                Toast.makeText(getApplicationContext(), "Error In Removing Friend", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });



        mStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                chatIntent.putExtra("user_id", userID);
                chatIntent.putExtra("name", mOUsernamebox.getText().toString());
                startActivity(chatIntent);
            }
        });


    }



    //TODO: REPLACE THIS WITH A TOOLBAR
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        }
        else{
            super.onBackPressed();
        }
    }
}
