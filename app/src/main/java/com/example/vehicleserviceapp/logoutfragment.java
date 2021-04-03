package com.example.vehicleserviceapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

public class logoutfragment extends Fragment {
    private CardView cardView;
    private Animation rotate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_logoutfragment, container, false);
        cardView=view.findViewById(R.id.logout_loading_card);
        rotate= AnimationUtils.loadAnimation(getContext(),R.anim.rotate_animation);
        cardView.startAnimation(rotate);
        doAccordingToType();
        return view;
    }
    private void doAccordingToType(){
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        String email= sharedPreferences.getString("Email","");
        FirebaseFirestore.getInstance().document("Users/"+email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userType=documentSnapshot.get("User Type").toString();
                if(userType.equals("Client"))
                    cancelClient();
                else if(userType.equals("Admin"))
                    cancelAdmin();
                FirebaseAuth.getInstance().signOut();
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().commit();
                Intent intent1=new Intent(getContext(),SignInSignUpActivity.class);
                startActivity(intent1);
                getActivity().finish();
            }
        });
    }
    private void cancelAdmin(){
        Intent intent=new Intent(getContext(),AdminBackgroundProcess.class);
        intent.setAction("adminBackgroundProcess");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getContext(),0,intent,0);
        AlarmManager alarmManager=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    private void cancelClient(){
        Intent intent=new Intent(getContext(),ClientBackgroundProcess.class);
        intent.setAction("clientBackgroundProcess");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getContext(),0,intent,0);
        AlarmManager alarmManager=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}