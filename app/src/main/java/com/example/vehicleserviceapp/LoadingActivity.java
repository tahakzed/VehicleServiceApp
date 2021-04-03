package com.example.vehicleserviceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoadingActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String email,password,mode;
    CardView cardView;
    Animation rotate;
    HashMap<String,Object> dbData;
    String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        cardView=findViewById(R.id.loading_card);
        rotate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_animation);
        cardView.startAnimation(rotate);
        getData();
        if(mode.equals("sign-in"))
            authUser();
        else if(mode.equals("sign-up"))
            createUser();
    }
    private void getData(){
        Intent intent=getIntent();
        email=intent.getStringExtra("Email");
        password=intent.getStringExtra("Password");
        mode=intent.getStringExtra("Mode");
        dbData=(HashMap<String, Object>) intent.getSerializableExtra("dbData");
        userType=intent.getStringExtra("UserType");
    }

    private void authUser(){
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                saveSharedPreferences();
                checkUserTypeAndLaunchUser(authResult.getUser().getEmail());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoadingActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoadingActivity.this,SignInSignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void createUser(){
        db.document(userType+"/"+email).set(dbData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoadingActivity.this,"Failed!",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoadingActivity.this,SignInSignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });//END: add to firestore

        //START: add user to firebase auth
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    saveSharedPreferences();
                    checkUserTypeAndLaunchUser(task.getResult().getUser().getEmail());
                }
                else{
                    Toast.makeText(LoadingActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoadingActivity.this,SignInSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void checkUserTypeAndLaunchUser(String emailId){
        db.document("Users/"+emailId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userType=documentSnapshot.get("User Type").toString();
                if(userType.equals("Client"))
                {
                    Intent intent=new Intent(LoadingActivity.this, ClientMainActivity.class);
                    intent.putExtra("Email",emailId);
                    startActivity(intent);
                    finish();
                }
                else if(userType.equals("Admin")){
                    Intent intent=new Intent(LoadingActivity.this, AdminMainActivity.class);
                    intent.putExtra("Email",emailId);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void saveSharedPreferences(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Email",email);
        editor.putString("Password",password);
        editor.apply();
    }
}