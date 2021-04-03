package com.example.vehicleserviceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends Fragment implements View.OnClickListener{
    private View view;
    private AutoCompleteTextView userType;
    private AutoCompleteTextView vehicleType;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String fullName,email,phone,password,userTypeStr,vehicleTypeStr,vehicleName,serviceStation;
    int chargesCar,chargesBike;
    private double lat,lng;
    private TextInputEditText nameET,emailET,phoneET,passET,vnameET,serviceStnET,chargesCarET,chargesBikeET;
    private Button signUpBtn,signInBtn,continueBtn1,continueBtn2;
    private static final int MAPS_ACTIVITY_REQUEST_CODE=101;
    private TextInputLayout tl,pl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_sign_up, container, false);
        signInBtn=view.findViewById(R.id.alr_reg_btn);

        signInBtn.setOnClickListener(this);
        signUpBtn=view.findViewById(R.id.or_sign_up_btn);
        signUpBtn.setOnClickListener(this);
        userType=view.findViewById(R.id.user_type_input);
        continueBtn1=view.findViewById(R.id.continue1_btn);
        continueBtn1.setOnClickListener(this);
        continueBtn2=view.findViewById(R.id.continue2_btn);
        continueBtn2.setOnClickListener(this);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),R.layout.list_item,new String[]{"Client","Admin"});
        userType.setAdapter(adapter);
        vehicleType=view.findViewById(R.id.vtype_input);
        ArrayAdapter<String> adapter1=new ArrayAdapter<String>(getContext(),R.layout.list_item,new String[]{"Car","Bike"});
        vehicleType.setAdapter(adapter1);
        nameET=view.findViewById(R.id.fname_input);
        emailET=view.findViewById(R.id.email_signup_input);
        phoneET=view.findViewById(R.id.phone_input);
        passET=view.findViewById(R.id.password_signup_input);
        vnameET=view.findViewById(R.id.vname_input);
        serviceStnET=view.findViewById(R.id.service_station_input);
        chargesCarET=view.findViewById(R.id.car_charges_input);
        chargesBikeET=view.findViewById(R.id.bike_charges_input);
        tl=view.findViewById(R.id.email_layout);
        pl=view.findViewById(R.id.pass_layout);
        db= FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();


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
            case R.id.alr_reg_btn:
                Fragment signInFragment=new SignInFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment,signInFragment)
                        .commit();
                break;
            case R.id.or_sign_up_btn:
                ViewGroup buserlayout=view.findViewById(R.id.basic_user_layout);
                userTypeStr=userType.getText().toString();
                fullName=nameET.getText().toString();
                email=emailET.getText().toString();
                phone=phoneET.getText().toString();
                password=passET.getText().toString();
                if(!validate(email))
                {
                    tl.setError("Invalid Email!");
                    return;
                }
                if(password.length()<6)
                {
                    pl.setError("Password must contain at least 6 characters!");
                    return;
                }
                buserlayout.setVisibility(LinearLayout.GONE);

                if(userTypeStr.equals("Client")) {
                    ViewGroup clientLayout=view.findViewById(R.id.client_layout);
                    clientLayout.setVisibility(LinearLayout.VISIBLE);
                }
                else if(userTypeStr.equals("Admin")){
                    ViewGroup adminLayout=view.findViewById(R.id.admin_layout);
                    adminLayout.setVisibility(LinearLayout.VISIBLE);
                }
                break;

            case R.id.continue1_btn:
                vehicleTypeStr=vehicleType.getText().toString();
                vehicleName=vnameET.getText().toString();
                Intent intent=new Intent(getContext(),MapsActivity.class);
                startActivityForResult(intent,MAPS_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.continue2_btn:
                serviceStation=serviceStnET.getText().toString();
                chargesCar=Integer.parseInt(chargesCarET.getText().toString());
                chargesBike=Integer.parseInt(chargesBikeET.getText().toString());
                Intent intent1=new Intent(getContext(),MapsActivity.class);
                startActivityForResult(intent1,MAPS_ACTIVITY_REQUEST_CODE);
                break;


        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MAPS_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            lat=data.getDoubleExtra("Lat",-1);
            lng=data.getDoubleExtra("Lng",-1);
            HashMap<String,Object> dbData=new HashMap<>();
            dbData.put("User Type",userTypeStr);
            dbData.put("Lat",lat);
            dbData.put("Lng",lng);

            if(userTypeStr.equals("Admin")){
                List<String> admin_reviews=new ArrayList<>();
                Admin admin=new Admin(fullName,email,phone,lat,lng,serviceStation,admin_reviews,chargesCar,chargesBike,new ArrayList<String>(),"");
                dbData.put("Name",admin.getName());
                dbData.put("Email",admin.getEmail());
                dbData.put("Phone",admin.getPhone());
                dbData.put("Reviews",admin_reviews);
                dbData.put("ImageId","");
                db.document("Users/"+admin.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();
                            //START: Checking if user exist
                            if(documentSnapshot.exists()){
                                Toast.makeText(getContext(),"User Already Exist!",Toast.LENGTH_SHORT).show();
                                Intent intent = getActivity().getIntent();
                                getActivity().getSupportFragmentManager().beginTransaction().remove(SignUpFragment.this).commit();
                                getActivity().finish();
                                startActivity(intent);
                            }//END: Checking if user exist
                            //START: User does not exist
                            else{
                                //START: add to firestore
                                db.document("Users/"+admin.getEmail()).set(dbData);
                                dbData.put("Service Station",admin.getServiceStationName());
                                dbData.put("Charges Car",chargesCar);
                                dbData.put("Charges Bike",chargesBike);
                                dbData.put("Bookings",new ArrayList<String>());
                                Intent loadingIntent=new Intent(getContext(),LoadingActivity.class);
                                loadingIntent.putExtra("dbData",dbData);
                                loadingIntent.putExtra("Email",email);
                                loadingIntent.putExtra("Password",password);
                                loadingIntent.putExtra("UserType",userTypeStr);
                                loadingIntent.putExtra("Mode","sign-up");
                                startActivity(loadingIntent);
                                getActivity().getSupportFragmentManager().beginTransaction().remove(SignUpFragment.this).commit();
                                getActivity().finish();

                            } //END: User does not exist
                        }
                    }
                });

            }
            else if(userTypeStr.equals("Client")){
                List<String> vehicleNameAndType=new ArrayList<>();
                vehicleNameAndType.add(vehicleName+";"+vehicleTypeStr);
                Client client=new Client(fullName,email,phone,lat,lng,vehicleNameAndType,new ArrayList<String>(),"");
                dbData.put("Name",client.getName());
                dbData.put("Email",client.getEmail());
                dbData.put("Phone",client.getPhone());
                dbData.put("ImageId","");
                dbData.put("Vehicles",client.getVehicleNameAndType());
                db.document("Users/"+client.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot=task.getResult();
                            //START: Checking if user exist
                            if(documentSnapshot.exists()){
                                Toast.makeText(getContext(),"User Already Exist!",Toast.LENGTH_SHORT).show();
                            }//END: Checking if user exist
                            //START: User does not exist
                            else{
                                //START: add to firestore
                                db.document("Users/"+client.getEmail()).set(dbData);
                                dbData.put("Vehicles",client.getVehicleNameAndType());
                                dbData.put("Bookings",client.getBookings());

                                Intent loadingIntent=new Intent(getContext(),LoadingActivity.class);
                                loadingIntent.putExtra("dbData",dbData);
                                loadingIntent.putExtra("Email",email);
                                loadingIntent.putExtra("Password",password);
                                loadingIntent.putExtra("UserType",userTypeStr);
                                loadingIntent.putExtra("Mode","sign-up");
                                startActivity(loadingIntent);
                                getActivity().getSupportFragmentManager().beginTransaction().remove(SignUpFragment.this).commit();
                                getActivity().finish();
                                //END: add user to firebase auth
                            } //END: User does not exist
                        }
                    }
                });
            }
        }
    }
    private void checkUserTypeAndLaunchUser(){

        if(userTypeStr.equals("Client"))
        {
            Intent intent=new Intent(getContext(), ClientMainActivity.class);
            intent.putExtra("Email",email);
            startActivity(intent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(SignUpFragment.this).commit();
            getActivity().finish();
        }
        else if(userTypeStr.equals("Admin")){
            Intent intent=new Intent(getContext(), AdminMainActivity.class);
            intent.putExtra("Email",email);
            startActivity(intent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(SignUpFragment.this).commit();
            getActivity().finish();
        }

    }
    private void saveSharedPreferences(String email,String password){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Email",email);
        editor.putString("Password",password);
    }
}