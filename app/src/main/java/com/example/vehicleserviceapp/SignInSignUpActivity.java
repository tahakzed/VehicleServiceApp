package com.example.vehicleserviceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SignInSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment,new SignInFragment())
                .commit();
    }
}