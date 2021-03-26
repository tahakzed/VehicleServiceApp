package com.example.vehicleserviceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class SplashScreen extends AppCompatActivity {
    Animation blink;
    FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        frameLayout=findViewById(R.id.blink_frame);
        blink= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bg_anim);
        frameLayout.startAnimation(blink);
        Handler handler=new Handler();
        handler.postDelayed(() -> {
            Intent intent=new Intent(SplashScreen.this,SignInSignUpActivity.class);
            startActivity(intent);
            finish();
        },2000);

    }
}