package com.example.abdullahshah.chatroughdraftnew;

import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.abdullahshah.chatroughdraftnew.Fragments.BookFragment;
import com.example.abdullahshah.chatroughdraftnew.Fragments.ChatFragment;
import com.example.abdullahshah.chatroughdraftnew.Fragments.FeedFragment;
import com.example.abdullahshah.chatroughdraftnew.Fragments.LookUpFragment;
import com.example.abdullahshah.chatroughdraftnew.Fragments.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private BookFragment bookFragment;
    private LookUpFragment lookUpFragment;
    private FeedFragment feedFragment;
    private ChatFragment chatFragment;
    private ProfileFragment profileFragment;

    private DatabaseReference mUserRef;

    private int chatIntentExtra;

    private SharedPreference mSharedPreference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSharedPreference = new SharedPreference(MainActivity.this);
        if(mSharedPreference.getApp_runFirst().equals("FIRST")){
            Toast.makeText(getApplicationContext(), "Running for first time", Toast.LENGTH_LONG);
            mSharedPreference.setApp_runFirst("NO");
        } else {

        }


        mAuth = FirebaseAuth.getInstance();

        mMainNav = findViewById(R.id.main_nav);
        mMainFrame = findViewById(R.id.main_frame);

        mMainNav.setItemBackgroundResource(R.color.endblue);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        } else {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("online").setValue("true");
        }

        //Init Fragments
        bookFragment = new BookFragment();
        lookUpFragment = new LookUpFragment();
        feedFragment = new FeedFragment();
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();



    }

    @Override
    public void onStart() {
        super.onStart();

        chatIntentExtra = getIntent().getIntExtra("COMINGBACKFROMCHATACTVITYSENDMETOCHATPLZ", 2);
        if(chatIntentExtra == 1){
            mMainNav.getMenu().getItem(3).setChecked(true);
            setFragment(chatFragment);
            ChatFragment.mViewPager.setCurrentItem(1);
        }


        // Check if user is signed in (non-null) and update UI accordingly.


        // For now, Nav Bar auto starts at profileFragment
        // Real App will have it start at Feed i.e setFragment(feedFragment);

        mMainNav.getMenu().getItem(2).setChecked(true);
        setFragment(feedFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_book:
                        setFragment(bookFragment);
                        return true;
                    case R.id.nav_look_up:
                        setFragment(lookUpFragment);
                        return true;
                    case R.id.nav_feed:
                        setFragment(feedFragment);
                        return true;
                    case R.id.nav_messages:
                        setFragment(chatFragment);
                        return true;
                    case R.id.nav_profile:
                        setFragment(profileFragment);
                        return true;
                    default:
                        return false;

                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            mUserRef.child("online").setValue("false");
            mUserRef.child("lastseen").setValue(ServerValue.TIMESTAMP);
        }
    }

    public void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);

        fragmentTransaction.commit();
    }
}
