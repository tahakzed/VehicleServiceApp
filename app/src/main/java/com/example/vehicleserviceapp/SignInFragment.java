package com.example.vehicleserviceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignInFragment extends Fragment implements View.OnClickListener{

    private String email,password;
    private TextInputEditText emailET,passwordET;
    private Button signInBtn,signUpBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputLayout tl;
    private TextInputLayout pl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_sign_in, container, false);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        tl=view.findViewById(R.id.client_email_signin_layout);
        pl=view.findViewById(R.id.client_pass_signin_layout);
        signUpBtn=view.findViewById(R.id.sign_up_btn);
        signUpBtn.setOnClickListener(this);
        signInBtn=view.findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);
        emailET=view.findViewById(R.id.email_input);
        passwordET=view.findViewById(R.id.password_input);

        return view;
    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.sign_up_btn:
                SignUpFragment signupFragment=new SignUpFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment,signupFragment)
                        .commit();
                break;
            case R.id.sign_in_btn:
                email=emailET.getText().toString();
                if(!validate(email)){
                    tl.setError("Invalid email!");
                    return;
                }
                password=passwordET.getText().toString();
                if(password.length()<6){
                    pl.setError("Password must contain at least 6 characters!");
                    return;
                }
                Intent loadingIntent=new Intent(getContext(),LoadingActivity.class);
                loadingIntent.putExtra("Email",email);
                loadingIntent.putExtra("Password",password);
                loadingIntent.putExtra("Mode","sign-in");
                startActivity(loadingIntent);
                getActivity().getSupportFragmentManager().beginTransaction().remove(SignInFragment.this).commit();
                getActivity().finish();


        }
    }

}