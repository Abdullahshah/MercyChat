package com.example.abdullahshah.chatroughdraftnew.Fragments.chatsubFragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdullahshah.chatroughdraftnew.ChatActivity;
import com.example.abdullahshah.chatroughdraftnew.Fragments.ChatFragment;
import com.example.abdullahshah.chatroughdraftnew.OtherUserActivity;
import com.example.abdullahshah.chatroughdraftnew.R;
import com.example.abdullahshah.chatroughdraftnew.models.Friend;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsSubFragment extends Fragment {

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private RecyclerView mFriendsList;
    private LinearLayoutManager mManager;
    private FirebaseUser mCurrent_user;

    Dialog showDialog;


    public FriendsSubFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_friends_sub, container, false);

        mFriendsList = (RecyclerView) rootView.findViewById(R.id.friends_list);
        mFriendsList.setHasFixedSize(true);
        showDialog = new Dialog(getContext());

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user.getUid());
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        mManager = new LinearLayoutManager(getActivity());
        //mManager.setReverseLayout(true);
        //mManager.setStackFromEnd(true);
        mFriendsList.setLayoutManager(mManager);

        Query query = mFriendsDatabase.limitToLast(50);


        FirebaseRecyclerOptions<Friend> options = new FirebaseRecyclerOptions.Builder<Friend>()
                .setQuery(query, Friend.class)
                .build();


        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull final Friend model) {
                holder.setDate(model.getDate());
                //holder.setName(model.getUsername());
                //holder.setStatus(model.getStatus());
                //holder.setUserImage(model.getThumb_image());

                final String friend_id = getRef(position).getKey();
                final DatabaseReference dialogFriendDatabase = FirebaseDatabase.getInstance()
                        .getReference().child("Users").child(friend_id);

                mUsersDatabase.child(friend_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("username").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }

                        holder.setName(userName);
                        holder.setUserImage(userThumb);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView txtclose;
                        final ImageView friendImage;
                        final TextView friendName;
                        final TextView friendFollowers;
                        final TextView friendFollowing;
                        final TextView friendUsername;
                        final TextView friendStatus;
                        final Button friendProfileBtn;

                        showDialog.setContentView(R.layout.profile_friendpopup);
                        txtclose = showDialog.findViewById(R.id.friendpopup_exit);
                        friendImage = showDialog.findViewById(R.id.friend_imagepopup);
                        friendName = showDialog.findViewById(R.id.nameBox_friendpopup);
                        friendFollowers = showDialog.findViewById(R.id.friend_followers_number);
                        friendFollowing = showDialog.findViewById(R.id.friend_following_number);
                        friendUsername = showDialog.findViewById(R.id.friend_username_popup);
                        friendStatus = showDialog.findViewById(R.id.friend_status_popup);
                        friendProfileBtn = showDialog.findViewById(R.id.viewfriendprofile_btn);

                        final ValueEventListener valueEventListener = new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String profileImagepath = dataSnapshot.child("image").getValue().toString();
                                friendName.setText(dataSnapshot.child("first_name").getValue().toString() +
                                        " " + dataSnapshot.child("last_name").getValue().toString());
                                friendFollowers.setText(dataSnapshot.child("followers").getValue().toString());
                                friendFollowing.setText(dataSnapshot.child("following").getValue().toString());
                                friendStatus.setText(dataSnapshot.child("status").getValue().toString());
                                friendUsername.setText("Message " + dataSnapshot.child("first_name").getValue().toString());
                                /*
                                if(checkifFriendinDatabase(friend_id)) {
                                    Toast.makeText(getContext(), "Friend", Toast.LENGTH_SHORT).show();
                                    friendUsername.setText("Open Chat");
                                } else {
                                    Toast.makeText(getContext(), "NOT Friend", Toast.LENGTH_SHORT).show();
                                    friendUsername.setText(dataSnapshot.child("username").getValue().toString());
                                }
                                */
                                if (!profileImagepath.equals("default")) {
                                    Picasso.get().load(profileImagepath).placeholder(R.drawable.default_profile).into(friendImage);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }

                        };

                        dialogFriendDatabase.addValueEventListener(valueEventListener);

                        friendUsername.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog.dismiss();

                                /*
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                Fragment chat = new ChatsSubFragment();
                                transaction.replace(R.id.chat_viewpager, chat);
                                transaction.commit();
                                */
                                //ChatFragment.mViewPager.setCurrentItem(1);

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id", friend_id);
                                chatIntent.putExtra("name", friendName.getText().toString());
                                startActivity(chatIntent);
                            }
                        });

                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                               dialogFriendDatabase.removeEventListener(valueEventListener);
                                showDialog.dismiss();
                            }
                        });
                        friendProfileBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogFriendDatabase.removeEventListener(valueEventListener);
                                showDialog.dismiss();
                                Intent friendProfileIntent = new Intent(getContext(), OtherUserActivity.class);
                                friendProfileIntent.putExtra("friendID", friend_id);
                                startActivity(friendProfileIntent);
                            }
                        });
                        showDialog.show();
                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_single_layout, parent, false);

                return new FriendsViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        mFriendsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setName(String name){
            TextView userNameView = mView.findViewById(R.id.friend_list_name);
            userNameView.setText(name);
        }
        public void setStatus(String status){
            TextView userStatusView = mView.findViewById(R.id.friend_list_status);
            userStatusView.setText(status);
        }
        public void setDate(String date){
            TextView userStatusView = mView.findViewById(R.id.friend_list_status);
            userStatusView.setText(date);
        }
        public void setUserImage(String thumb_image){
            CircleImageView userImageView = mView.findViewById(R.id.friend_list_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.default_profile).into(userImageView);
        }
        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.friend_list_online);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }
    }

}
