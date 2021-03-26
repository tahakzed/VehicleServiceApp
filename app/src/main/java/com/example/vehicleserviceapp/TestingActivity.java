package com.example.vehicleserviceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.testing_fragment,new BookingFragment())
                .addToBackStack(null)
                .commit();
    }
}