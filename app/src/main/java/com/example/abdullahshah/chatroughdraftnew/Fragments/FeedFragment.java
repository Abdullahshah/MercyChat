package com.example.abdullahshah.chatroughdraftnew.Fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.abdullahshah.chatroughdraftnew.MainActivity;
import com.example.abdullahshah.chatroughdraftnew.NewPostActivity;
import com.example.abdullahshah.chatroughdraftnew.OtherUserActivity;
import com.example.abdullahshah.chatroughdraftnew.R;
import com.example.abdullahshah.chatroughdraftnew.models.Community;
import com.example.abdullahshah.chatroughdraftnew.models.Post;
import com.example.abdullahshah.chatroughdraftnew.models.PostHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {

    private RecyclerView mFeedlist;
    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Post, PostHolder> mAdapter;

    private FirebaseAuth mAuth;
    private String mCurrent_User;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFeedDatabase;
    private String Community_name;

    Dialog showDialog;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mFeedlist = rootView.findViewById(R.id.feedRecylerView);
        mFeedlist.setHasFixedSize(true);

        mCurrent_User = mAuth.getInstance().getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_User);
        mUsersDatabase.keepSynced(true);


        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("community_chosen")){
                    if(dataSnapshot.child("community_chosen").equals("")) {
                        Community_name = "Meme-Feed";
                    }
                    else{
                        Community_name = dataSnapshot.child("community_chosen").getValue().toString();
                    }
                } else {
                    Community_name = "Meme-Feed";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("DATA CANCELLED", "DATA CANCELLED");
                Community_name = "Meme-Feed";

            }
        });
        if(Community_name ==  null){
            Community_name = "Meme-Feed";
        }
        mFeedDatabase = FirebaseDatabase.getInstance().getReference().child("Communities").child(Community_name);
        mFeedDatabase.keepSynced(true);

        showDialog = new Dialog(getContext());


        rootView.findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(getContext(), NewPostActivity.class);
                newPostIntent.putExtra("Community_Name", Community_name);
                startActivity(newPostIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mFeedlist.setLayoutManager(mManager);

        Query postsQuery = mFeedDatabase.limitToLast(50);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<Post, PostHolder>(options) {
            @NonNull
            @Override
            public PostHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new PostHolder(inflater.inflate(R.layout.post_single_layout, viewGroup, false));
            }
            @Override
            protected void onBindViewHolder(@NonNull PostHolder viewHolder, int position, @NonNull final Post model) {
                final DatabaseReference postRef = getRef(position);
                final String postKey = postRef.getKey();

                viewHolder.bindToPost(model, postKey, Community_name, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final DatabaseReference userpopupDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getuID());
                        TextView txtclose;
                        final ImageView friendImage;
                        final TextView friendName;
                        final TextView friendFollowers;
                        final TextView friendFollowing;
                        final TextView friendUsername;
                        final TextView friendStatus;
                        final Button friendProfileBtn;

                        showDialog.setContentView(R.layout.profile_other_userpopup);
                        txtclose = showDialog.findViewById(R.id.otheruserpopup_exit);
                        friendImage = showDialog.findViewById(R.id.otheruser_imagepopup);
                        friendName = showDialog.findViewById(R.id.other_Friend_namebox);
                        friendFollowers = showDialog.findViewById(R.id.otheruser_followers_numbers);
                        friendFollowing = showDialog.findViewById(R.id.otheruser_following_number);
                        friendUsername = showDialog.findViewById(R.id.otheruser_username_popup);
                        friendProfileBtn = showDialog.findViewById(R.id.otheruser_profile_btn);

                        ImageView usernamelogo = showDialog.findViewById(R.id.otheruser_username_popuplogo);
                        usernamelogo.setImageResource(R.drawable.ic_person_startblue_24dp);

                        final ValueEventListener valueEventListener = new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String profileImagepath = dataSnapshot.child("image").getValue().toString();
                                friendName.setText(dataSnapshot.child("first_name").getValue().toString() +
                                        " " + dataSnapshot.child("last_name").getValue().toString());
                                friendFollowers.setText(dataSnapshot.child("followers").getValue().toString());
                                friendFollowing.setText(dataSnapshot.child("following").getValue().toString());
                                friendUsername.setText(dataSnapshot.child("username").getValue().toString());

                                if (!profileImagepath.equals("default")) {
                                    Picasso.get().load(profileImagepath).placeholder(R.drawable.default_profile).into(friendImage);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }

                        };
                        userpopupDatabase.addValueEventListener(valueEventListener);

                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                userpopupDatabase.removeEventListener(valueEventListener);
                                showDialog.dismiss();
                            }
                        });
                        friendProfileBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                userpopupDatabase.removeEventListener(valueEventListener);
                                showDialog.dismiss();
                                Intent friendProfileIntent = new Intent(getContext(), OtherUserActivity.class);
                                friendProfileIntent.putExtra("friendID", model.getuID());
                                startActivity(friendProfileIntent);
                            }
                        });
                        showDialog.show();
                        return true;
                    }
                });
            }
        };
        mFeedlist.setAdapter(mAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
