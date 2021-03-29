package com.example.vehicleserviceapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AdminProfileFragment extends Fragment implements View.OnClickListener {

    private String serviceStationName,email,phone,address;
    private float rating;
    private long chargesCar,chargesBike;
    private List<String> reviewsList;
    private List<String> bookingsList;
    private EditText stationNameTv,emailTv,phoneTv,addressTv,chargesTv;
    private TextView ratingTv,bookingCountTv;
    private RatingBar ratingBar;
    private RecyclerView recyclerView;
    private ReviewsRecyclerAdapter adapter;
    private View view;
    private double lat,lng;
    private FloatingActionButton settingsFab;
    private int FAB_CLICK_FLAG=0;
    private static final int MAPS_ACTIVITY_REQUEST_CODE=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_admin_profile, container, false);
        init();
        getData();
        setData();
        return view;
    }

    private void init(){
        stationNameTv=view.findViewById(R.id.admin_profile_sevice_station_name);
        emailTv=view.findViewById(R.id.admin_profile_email);
        phoneTv=view.findViewById(R.id.admin_profile_phone);
        addressTv=view.findViewById(R.id.admin_profile_address);
        ratingTv=view.findViewById(R.id.admin_profile_rating_bar_textview);
        bookingCountTv=view.findViewById(R.id.admin_profile_booking_count);
        ratingBar=view.findViewById(R.id.admin_profile_rating_bar);
        recyclerView=view.findViewById(R.id.admin_profile_reviews_recycler_view);
        chargesTv=view.findViewById(R.id.admin_profile_charges);
        settingsFab=view.findViewById(R.id.admin_profile_settings_fab);
        settingsFab.setOnClickListener(this);
    }

    private void getData(){
        Bundle args=AdminProfileFragmentArgs.fromBundle(getArguments()).getAdminProfileData();
        serviceStationName=args.getString("serviceStationName");
        email=args.getString("email");
        phone=args.getString("phone");
        lat=args.getDouble("lat");
        lng=args.getDouble("lng");
        address=getAddress(lat,lng).getAddressLine(0);
        reviewsList=args.getStringArrayList("reviews");
        rating=calculateRating();
        chargesCar=args.getLong("chargesCar");
        chargesBike=args.getLong("chargesBike");
        bookingsList=args.getStringArrayList("bookings");
        adapter=new ReviewsRecyclerAdapter(reviewsList);

    }

    private void setData(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        stationNameTv.setText(serviceStationName);
        ratingBar.setRating(rating);
        ratingTv.setText("("+String.valueOf(rating)+")");
        bookingCountTv.setText("Bookings("+bookingsList.size()+")");
        emailTv.setText(email);
        phoneTv.setText(phone);
        addressTv.setText(address);
        chargesTv.setText("Car: "+chargesCar+", Bike: "+chargesBike);

    }
    private float calculateRating(){
        float star5=0,star4=0,star3=0,star2=0,star1=0;
        for(String str : reviewsList)
        {
            String[] arr=str.split(";");
            if(arr[1].equals("5.0"))
                star5++;
            else if(arr[1].equals("4.0"))
                star4++;
            else if(arr[1].equals("3.0"))
                star3++;
            else if(arr[1].equals("2.0"))
                star4++;
            else if(arr[1].equals("1.0"))
                star1++;
        }

        return (5*star5+4*star4+3*star3+2*star2+1*star1)/reviewsList.size();
    }
    private Address getAddress(double lat, double lng){
        Geocoder geo=new Geocoder(getContext(), Locale.getDefault());
        Address address=new Address(Locale.getDefault());
        try{
            List<Address> addresses=geo.getFromLocation(lat,lng,1);
            address= addresses.get(0);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return address;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.admin_profile_settings_fab:
                if(FAB_CLICK_FLAG==0){

                stationNameTv.setEnabled(true);
                phoneTv.setEnabled(true);
                addressTv.setEnabled(true);
                chargesTv.setEnabled(true);
                addressTv.setOnClickListener(this);
                chargesTv.setOnClickListener(this);

                FAB_CLICK_FLAG=1;
                settingsFab.setImageResource(R.drawable.done_icon);
                }
                else if(FAB_CLICK_FLAG==1)
                {
                    phone=phoneTv.getText().toString();
                    address=addressTv.getText().toString();
                    serviceStationName=stationNameTv.getText().toString();
                    String charges="Car:"+chargesCar+", Bike:"+chargesBike;
                    Map<String,Object> adminMap=new HashMap<>();
                    adminMap.put("Email",email);
                    adminMap.put("Service Station",serviceStationName);
                    adminMap.put("Phone",phone);
                    adminMap.put("Charges Car",chargesCar);
                    adminMap.put("Charged Bike",chargesBike);
                    adminMap.put("Lat",lat);
                    adminMap.put("Lng",lng);
                    FirebaseFirestore.getInstance().collection("Admin")
                            .document(email).update(adminMap);
                    phoneTv.setText(phone);
                    addressTv.setText(address);
                    stationNameTv.setText(serviceStationName);
                    chargesTv.setText(charges);
                    stationNameTv.setEnabled(false);
                    phoneTv.setEnabled(false);
                    addressTv.setEnabled(false);
                    chargesTv.setEnabled(false);
                    chargesTv.setOnClickListener(null);
                    addressTv.setOnClickListener(null);
                    FAB_CLICK_FLAG=0;
                    settingsFab.setImageResource(R.drawable.settings_icon);
                    Toast.makeText(getContext(),"Changes Saved!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.admin_profile_address:
                Intent intent1=new Intent(getContext(),MapsActivity.class);
                startActivityForResult(intent1,MAPS_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.admin_profile_charges:
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                View view=LayoutInflater.from(getContext())
                        .inflate(R.layout.price_settings_dialog_view,null,false);
                EditText carPrice=view.findViewById(R.id.admin_settings_car_price);
                EditText bikePrice=view.findViewById(R.id.admin_settings_bike_price);
                carPrice.setText(String.valueOf(chargesCar));
                bikePrice.setText(String.valueOf(chargesBike));
                builder.setView(view);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chargesCar=Long.parseLong(carPrice.getText().toString());
                        chargesBike=Long.parseLong(bikePrice.getText().toString());
                        String charges="Car:"+chargesCar+", Bike:"+chargesBike;
                        chargesTv.setText(charges);
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MAPS_ACTIVITY_REQUEST_CODE && requestCode==getActivity().RESULT_OK)
        {
            lat=data.getDoubleExtra("Lat",-1);
            lng=data.getDoubleExtra("Lng",-1);
            address=getAddress(lat,lng).getAddressLine(0);
            addressTv.setText(address);
        }
    }
}