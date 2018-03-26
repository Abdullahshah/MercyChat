package com.example.abdullahshah.chatroughdraftnew;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jaeger.library.StatusBarUtil;

public class StartActivity extends AppCompatActivity {

    private Button mRegButton;
    private Button mSigninButton;
    private ConstraintLayout constraintLayout;
    private Handler delayhandler;
    private Runnable run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        StatusBarUtil.setTransparent(StartActivity.this);

        constraintLayout = findViewById(R.id.relativelayout_constraintactivity);
        mRegButton = findViewById(R.id.RegBtn);
        mSigninButton = findViewById(R.id.loginBtn);

        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        int newColors[] = new ColorWheel().getColor();

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                newColors);
        gd.setCornerRadius(0f);
        constraintLayout.setBackground(gd);

        delayhandler = new Handler();
        run = new Runnable() {
            @Override
            public void run() {
                loop();
            }
        };
        delayhandler.postDelayed(run, 2000);

    }
    private void loop(){
        int newColors[] = new ColorWheel().getColor();

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                newColors);
        gd.setCornerRadius(0f);

        constraintLayout.setBackground(gd);
        delayhandler.postDelayed(run, 2000);

    }


}
