package com.example.abdullahshah.chatroughdraftnew.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abdullahshah.chatroughdraftnew.Fragments.chatsubFragments.ChatsSubFragment;
import com.example.abdullahshah.chatroughdraftnew.Fragments.chatsubFragments.FriendsSubFragment;
import com.example.abdullahshah.chatroughdraftnew.Fragments.chatsubFragments.RequestsSubFragment;
import com.example.abdullahshah.chatroughdraftnew.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private FragmentPagerAdapter mPagerAdapter;
    public static ViewPager mViewPager;
    private TabLayout mTabLayout;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mViewPager = rootView.findViewById(R.id.chat_viewpager);
        mTabLayout = rootView.findViewById(R.id.main_tabs);

        mPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{
                    new RequestsSubFragment(),
                    new ChatsSubFragment(),
                    new FriendsSubFragment(),
            };
            private final String[] mFragmentNames = new String[]{
                    "Requests",
                    "Chats",
                    "Friends"
            };
            @Override
            public Fragment getItem(int position) {
                Log.i("getItem method called with Fragment:", String.valueOf(position));
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }
            public CharSequence getPageTitle(int position){
                Log.i("getPageTitled", "called" + String.valueOf(position));
                return mFragmentNames[position];
            }
        };

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Requests");
        mTabLayout.getTabAt(1).setText("Chats");
        mTabLayout.getTabAt(2).setText("Friends");


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("Current Item:", String.valueOf(mViewPager.getCurrentItem()));
            }

            @Override
            public void onPageSelected(int position) {
                Log.i("Selected", String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.i("ScrollStatechange:", String.valueOf(state));
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
