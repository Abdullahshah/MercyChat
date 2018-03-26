package com.example.abdullahshah.chatroughdraftnew.models;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdullahshah.chatroughdraftnew.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public CircleImageView profileView;
    public ImageView imageView;
    public TextView bodyView;

    public PostHolder(View itemView) {
        super(itemView);
        titleView   = itemView.findViewById(R.id.post_title);
        authorView  = itemView.findViewById(R.id.post_author);
        profileView = itemView.findViewById(R.id.post_author_photo);
        imageView = itemView.findViewById(R.id.post_image);
        bodyView    = itemView.findViewById(R.id.post_body);
    }
    public void bindToPost(Post post, String postkey, String Community_name, View.OnLongClickListener profilepicClickListener) {
        //(Post post, String community_name, String profileImagePath, String postImagePath)
        titleView.setText(post.title);
        authorView.setText(post.author);
        bodyView.setText(post.description);

        DatabaseReference mFeedDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Communities").child(Community_name).child(postkey);
        DatabaseReference mUsersDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(post.uID);

        mFeedDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("image")){
                    String postimagepath = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(postimagepath).placeholder(R.drawable.newpost_imagebutton).into(imageView);

                } else{
                    imageView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("image")) {
                    String profileimagepath = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(profileimagepath).placeholder(R.drawable.default_profile).into(profileView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profileView.setOnLongClickListener(profilepicClickListener);


        //Picasso.get().load(profileImagePath).placeholder(R.drawable.default_profile).into(profileView);
        //Picasso.get().load(postImagePath).placeholder(R.drawable.newpost_imagebutton).into(imageView);


    }

}
