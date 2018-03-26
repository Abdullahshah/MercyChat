package com.example.abdullahshah.chatroughdraftnew.Fragments.chatsubFragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.abdullahshah.chatroughdraftnew.Fragments.ChatFragment;
import com.example.abdullahshah.chatroughdraftnew.R;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsSubFragment extends Fragment {

    private Button button;
    private RecyclerView mReqList;

    private FirebaseAuth mAuth;
    private String mCurrent_user_id;


    public RequestsSubFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_requests_sub, container, false);

        mReqList = (RecyclerView) rootView.findViewById(R.id.req_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();



        return rootView;
    }

}
