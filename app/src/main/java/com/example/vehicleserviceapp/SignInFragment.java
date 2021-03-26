package com.example.vehicleserviceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInFragment extends Fragment implements View.OnClickListener{

    private String email,password;
    private TextInputEditText emailET,passwordET;
    private Button signInBtn,signUpBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sign_in, container, false);
        signUpBtn=view.findViewById(R.id.sign_up_btn);
        signUpBtn.setOnClickListener(this);
        signInBtn=view.findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);
        emailET=view.findViewById(R.id.email_input);
        passwordET=view.findViewById(R.id.password_input);
        mAuth=FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.sign_up_btn:
                Fragment signupFragment=new SignUpFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment,signupFragment)
                        .commit();
                break;
            case R.id.sign_in_btn:
                email=emailET.getText().toString();
                password=passwordET.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent=new Intent(getContext(), ClientMainActivity.class);
                        intent.putExtra("Email",email);
                        startActivity(intent);
                        getActivity().getSupportFragmentManager().beginTransaction().remove(SignInFragment.this).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


        }
    }
}