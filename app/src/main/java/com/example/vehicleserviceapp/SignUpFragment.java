package com.example.vehicleserviceapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        db= FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        return view;
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
                buserlayout.setVisibility(LinearLayout.GONE);
                userTypeStr=userType.getText().toString();
                fullName=nameET.getText().toString();
                email=emailET.getText().toString();
                phone=phoneET.getText().toString();
                password=passET.getText().toString();
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
            Map<String,Object> dbData=new HashMap<>();
            dbData.put("User Type",userTypeStr);
            dbData.put("Lat",lat);
            dbData.put("Lng",lng);

            if(userTypeStr.equals("Admin")){
                List<String> admin_reviews=new ArrayList<>();
                admin_reviews.add(" ;NaN; ");
                Admin admin=new Admin(fullName,email,phone,lat,lng,serviceStation,admin_reviews,chargesCar,chargesBike,new ArrayList<String>());
                dbData.put("Name",admin.getName());
                dbData.put("Email",admin.getEmail());
                dbData.put("Phone",admin.getPhone());
                dbData.put("Reviews",admin_reviews);
                db.document("Users/"+admin.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                db.document("Users/"+admin.getEmail()).set(dbData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"record added!",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                });//END: add to firestore
                                dbData.put("Service Station",admin.getServiceStationName());
                                dbData.put("Charges Car",chargesCar);
                                dbData.put("Charges Bike",chargesBike);
                                dbData.put("Bookings",new ArrayList<String>());
                                db.document("Admin/"+admin.getEmail()).set(dbData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"record added!",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                });//END: add to firestore
                                //START: add user to firebase auth
                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(),"User Created",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //END: add user to firebase auth
                            } //END: User does not exist
                        }
                    }
                });
                Fragment mainFragment=new MainFragment();
                Bundle bundle=new Bundle();
                bundle.putString("Name",admin.getName());
                bundle.putString("Email",admin.getEmail());
                bundle.putString("Phone",admin.getPhone());
                bundle.putString("User Type",userTypeStr);
                bundle.putString("Location",lat+";"+lng);

                mainFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment,mainFragment)
                        .commit();
            }
            else if(userTypeStr.equals("Client")){
                List<String> vehicleNameAndType=new ArrayList<>();
                vehicleNameAndType.add(vehicleName+";"+vehicleTypeStr);
                Client client=new Client(fullName,email,phone,lat,lng,vehicleNameAndType,new ArrayList<String>());
                dbData.put("Name",client.getName());
                dbData.put("Email",client.getEmail());
                dbData.put("Phone",client.getPhone());
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
                                db.document("Users/"+client.getEmail()).set(dbData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"record added!",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                });//END: add to firestore
                                //START: add to firestore
                                dbData.put("Vehicles",client.getVehicleNameAndType());
                                dbData.put("Bookings",client.getBookings());
                                db.document("Client/"+client.getEmail()).set(dbData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"record added!",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Failed!",Toast.LENGTH_SHORT).show();
                                    }
                                });//END: add to firestore
                                //START: add user to firebase auth
                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(),"User Created",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                //END: add user to firebase auth
                            } //END: User does not exist
                        }
                    }
                });
            }
        }
    }
}