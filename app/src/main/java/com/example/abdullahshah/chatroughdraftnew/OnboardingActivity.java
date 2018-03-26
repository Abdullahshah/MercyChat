package com.example.abdullahshah.chatroughdraftnew;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnboardingActivity extends AppCompatActivity {

    public static ViewPager slideViewPager;
    private LinearLayout dotLayout;

    private SliderAdapater sliderAdapater;

    private TextView[] dots;

    private Button nextButton;
    private Button backButton;

    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);


        slideViewPager = findViewById(R.id.slideViewPager);
        dotLayout = findViewById(R.id.dotsLayout);

        sliderAdapater = new SliderAdapater(this);
        slideViewPager.setAdapter(sliderAdapater);

        addDotsIndicator(0);

        slideViewPager.addOnPageChangeListener(viewListener);

        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.previousButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nextButton.getText().toString().equals("Let's Start")){
                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                }else {
                    slideViewPager.setCurrentItem(currentPage + 1);
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideViewPager.setCurrentItem(currentPage - 1);
            }
        });

    }

    public void addDotsIndicator(int position){

        dots = new TextView[3];
        dotLayout.removeAllViews();

        for(int i = 0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorWhiteTransparent));

            dotLayout.addView(dots[i]);
        }
        if(dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);
            currentPage = position;

            if(currentPage == 0){

                nextButton.setEnabled(true);
                backButton.setEnabled(false);
                backButton.setVisibility(View.INVISIBLE);

                nextButton.setText("Next");
                backButton.setText("");
            } else if (position == dots.length - 1){

                nextButton.setEnabled(true);
                backButton.setEnabled(true);
                backButton.setVisibility(View.VISIBLE);

                nextButton.setText("Let's Start");
                backButton.setText("Previous");
            } else{

                nextButton.setEnabled(true);
                backButton.setEnabled(true);
                backButton.setVisibility(View.VISIBLE);

                nextButton.setText("Next");
                backButton.setText("Previous");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public class SliderAdapater extends PagerAdapter {

        Context context;
        LayoutInflater layoutInflater;

        public int[] slide_images = {
                R.drawable.book_logo,
                R.drawable.feedimage,
                R.drawable.messages

        };
        public String[] slide_headings = {
                "Book",
                "Feed",
                "Chat"
        };
        public String[] slide_descs = {
                "Read up upon what depression is and how to treat it. Explore how Islam defines and qualifies depression and other mental issues",
                "Access a feed of posts of your friends and get insight into cool posts. See really cool posts!",
                "Talk to your friends and family! Catch up on what recently happened or engage your friends in an interesting conversation"
        };

        public SliderAdapater(Context context){

            this.context = context;
        }

        @Override
        public int getCount() {
            return slide_headings.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.slide_layout, container, false);


            ImageView slideImageView = view.findViewById(R.id.slide_image);
            TextView slideHeading = view.findViewById(R.id.slide_heading);
            TextView slideDescription = view.findViewById(R.id.slide_desc);

            slideImageView.setImageResource(slide_images[position]);
            slideHeading.setText(slide_headings[position]);
            slideDescription.setText(slide_descs[position]);

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
