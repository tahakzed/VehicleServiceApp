package com.example.vehicleserviceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {
    Animation fadeIn;
    CardView cardView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        cardView=findViewById(R.id.splash_card);
        fadeIn=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_anim);
        cardView.startAnimation(fadeIn);
        launchWithSharedPreferences();

    }
    private void launchWithSharedPreferences(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String emailId=sharedPreferences.getString("Email","");
        String passwordd=sharedPreferences.getString("Password","");
        if(!emailId.equals("") && !passwordd.equals(""))
        {

            mAuth.signInWithEmailAndPassword(emailId, passwordd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    checkUserTypeAndLaunchUser(emailId);
                }
            });
        }
        else
        {
            Handler handler=new Handler();
            handler.postDelayed(() -> {
                Intent intent =new Intent(SplashScreen.this,SignInSignUpActivity.class);
                startActivity(intent);
                finish();
            },2000);

        }
    }
    private void checkUserTypeAndLaunchUser(String emailId){
        db.document("Users/"+emailId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userType=documentSnapshot.get("User Type").toString();
                if(userType.equals("Client"))
                {
                    Intent intent=new Intent(SplashScreen.this, ClientMainActivity.class);
                    intent.putExtra("Email",emailId);
                    startActivity(intent);
                    finish();
                }
                else if(userType.equals("Admin")){
                    Intent intent=new Intent(SplashScreen.this, AdminMainActivity.class);
                    intent.putExtra("Email",emailId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}