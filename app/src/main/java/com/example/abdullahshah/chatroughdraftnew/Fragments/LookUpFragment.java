package com.example.abdullahshah.chatroughdraftnew.Fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.abdullahshah.chatroughdraftnew.R;
import com.example.abdullahshah.chatroughdraftnew.models.Community;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LookUpFragment extends Fragment {

    Dialog mDialog;

    public LookUpFragment() {
        // Required empty public constructor
    }

   private EditText mSearchField;
   private LinearLayoutManager mManager;
   private RecyclerView mResultList;
   private FirebaseRecyclerAdapter<Community, CommunityViewHolder> mAdapter;
   private DatabaseReference mUsersDatabase;
   private FirebaseUser mAuth;


    private DatabaseReference mCommunityDatabse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_look_up, container, false);

        mSearchField = rootView.findViewById(R.id.search_field);

        mResultList = rootView.findViewById(R.id.community_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(getContext()));

        mCommunityDatabse = FirebaseDatabase.getInstance().getReference().child("CommunityInfo");
        mCommunityDatabse.keepSynced(true);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        String mCurrentUser = mAuth.getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser);



        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mResultList.setLayoutManager(mManager);

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString();
                Log.i("SearchText:", searchText);
                firebaseCommunitysearch(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void firebaseCommunitysearch(String searchText) {
        //Query firebaseSearchQuery = mCommunityDatabse.orderByChild("Communities").startAt(searchText).endAt(searchText + "\uf8ff");
        Query query = mCommunityDatabse.limitToLast(10);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Community>()
                .setQuery(query, Community.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Community, CommunityViewHolder>(options) {
            @NonNull
            @Override
            public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new CommunityViewHolder(inflater.inflate(R.layout.community_single_layout, viewGroup, false));
            }
            @Override
            protected void onBindViewHolder(@NonNull CommunityViewHolder holder, int position, @NonNull final Community model) {
                final DatabaseReference postRef = getRef(position);
                final String postKey = postRef.getKey();
                holder.setDetails(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog showDialog = new Dialog(getContext());
                        showDialog.setContentView(R.layout.communitysearchlayoutpopup);
                        final TextView communitypopuptitle = showDialog.findViewById(R.id.communitypopuptitle);
                        TextView communitypopupdescription = showDialog.findViewById(R.id.communitypopupdescription);
                        TextView communityexit = showDialog.findViewById(R.id.communitypopup_exit);
                        Button saveEditbutn = showDialog.findViewById(R.id.communitypopup_btn);

                        communitypopuptitle.setText(model.getTitle());
                        communitypopupdescription.setText(model.getDescription());
                        showDialog.setCanceledOnTouchOutside(true);
                        showDialog.show();

                        communityexit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog.hide();
                            }
                        });

                        saveEditbutn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mUsersDatabase.child("community_chosen").setValue(communitypopuptitle.getText().toString());
                                showDialog.hide();
                            }
                        });

                    }
                });
                

            }
        };

        mResultList.setAdapter(mAdapter);
    }
    public static class CommunityViewHolder extends RecyclerView.ViewHolder{

        TextView community_title;
        TextView community_description;

        public CommunityViewHolder(View itemView) {
            super(itemView);
            community_title = itemView.findViewById(R.id.community_title_layout);
            community_description = itemView.findViewById(R.id.community_description_layout);
        }
        public void setDetails(Community community, View.OnClickListener communityaddlistener){
            community_title.setText(community.title);
            community_description.setText(community.description);
            itemView.setOnClickListener(communityaddlistener);
        }
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
