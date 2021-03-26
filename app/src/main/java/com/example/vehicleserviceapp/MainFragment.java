package com.example.vehicleserviceapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainFragment extends Fragment {
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main, container, false);
        textView=view.findViewById(R.id.user_data);
        Bundle bundle=getArguments();
        textView.setText("Name: "+bundle.getString("Name")+"\n" +
                "Email: "+bundle.getString("Email")+"\n" +
                "Phone: "+bundle.getString("Phone")+"\n" +
                "Location: "+bundle.getString("Location")+"\n" +
                "UserType: "+bundle.getString("User Type")+"\n" +
                "Address: "+bundle.getString("Address"));
        return view;
    }
}