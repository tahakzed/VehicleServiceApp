package com.example.vehicleserviceapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class ClientProfileFragment extends Fragment implements View.OnClickListener {
    private EditText profileNameTv;
    private EditText profileEmailTv;
    private EditText profilePhoneTv;
    private EditText profileAddressTv;
    private TextView profileBookingCountTv;
    private ImageView profilePhoto;
    private ImageView profilePhotoSettings;
    private String name,email,phone,address,imageId;
    private Uri imageUri;
    private double lat,lng;
    private int bookingCount;
    private List<String> bookingIds;
    private View view;
    private FloatingActionButton settingsFab;
    private int FAB_CLICKED_FLAG=0;
    private int MAPS_ACTIVITY_REQUEST_CODE=1;
    private int PICK_IMAGE_REQUEST=2;
    private FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_client_profile, container, false);
        init();
        getData();
        setData();
        return view;
    }
    private void init(){
        profileNameTv=view.findViewById(R.id.profile_display_name);
        profileEmailTv=view.findViewById(R.id.profile_email);
        profilePhoneTv=view.findViewById(R.id.profile_phone);
        profileAddressTv=view.findViewById(R.id.profile_address);
        profileBookingCountTv=view.findViewById(R.id.profile_bookings_count);
        profilePhoto=view.findViewById(R.id.client_photo);
        profilePhotoSettings=view.findViewById(R.id.select_photo_icon);
        settingsFab=view.findViewById(R.id.profile_settings_fab);
        settingsFab.setOnClickListener(this);
    }
    private void getData(){
        Bundle data=ClientProfileFragmentArgs.fromBundle(getArguments()).getClientData();
        name=data.getString("clientName");
        email=data.getString("clientEmail");
        phone=data.getString("clientPhone");
        imageId=data.getString("clientImageId");
        lat=data.getDouble("Lat");
        lng=data.getDouble("Lng");
        address=getAddress(lat,lng).getAddressLine(0);
        bookingIds=data.getStringArrayList("bookings");
        bookingCount=bookingIds.size();

    }
    private void setData(){
    profileAddressTv.setText(address);
    profileBookingCountTv.setText("Ongoing bookings:"+bookingCount);
    profilePhoneTv.setText(phone);
    profileEmailTv.setText(email);
    profileNameTv.setText(name);
        //load image from firebase
        StorageReference storageReference=FirebaseStorage.getInstance().getReference();
        StorageReference ref=storageReference.child("images/"+imageId);
        GlideApp.with(this)
                .load(ref)
                .into(profilePhoto);
        //load image from firebase
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MAPS_ACTIVITY_REQUEST_CODE && requestCode==getActivity().RESULT_OK)
        {
            lat=data.getDoubleExtra("Lat",-1);
            lng=data.getDoubleExtra("Lng",-1);
            address=getAddress(lat,lng).getAddressLine(0);
            profileAddressTv.setText(address);
        }
        else if(requestCode==PICK_IMAGE_REQUEST && resultCode==getActivity().RESULT_OK && data!=null){
            if(data.getData()==null)
                return;
            imageUri=data.getData();
            profilePhoto.setImageURI(imageUri);
        }
    }
    private void updateBookings(){
        Map<String,Object> clientMap=new HashMap<>();
        clientMap.put("Client Name",name);
        clientMap.put("Client Phone",phone);
        clientMap.put("Client Image Id",imageId);
        clientMap.put("Lat",lat);
        clientMap.put("Lng",lng);
        for(String id: bookingIds){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    db.collection("Bookings")
                            .document(id).update(clientMap);
                }
            }).start();
        }
    }

    private void uploadImage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseStorage storage=FirebaseStorage.getInstance();
                StorageReference storageReference=storage.getReference();
                imageId= UUID.randomUUID().toString();
                StorageReference ref=storageReference
                        .child("images/"+imageId);
                ref.putFile(imageUri);
            }
        }).start();

    }
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.profile_settings_fab:
                if(FAB_CLICKED_FLAG==0){
                    profileNameTv.setEnabled(true);
                    profilePhoneTv.setEnabled(true);
                    profilePhoto.setAlpha((float) 0.6);
                    profilePhoto.setOnClickListener(this);
                profileAddressTv.setOnClickListener(this);
                settingsFab.setImageResource(R.drawable.done_icon);
                FAB_CLICKED_FLAG=1;
                }
                else if(FAB_CLICKED_FLAG==1)
                {
                    if(imageUri!=null)
                        uploadImage();
                    name=profileNameTv.getText().toString();
                    phone=profilePhoneTv.getText().toString();
                    Map<String,Object> clientMap=new HashMap<>();
                    clientMap.put("Name",name);
                    clientMap.put("ImageId",imageId);
                    clientMap.put("Phone",phone);
                    clientMap.put("Lat",lat);
                    clientMap.put("Lng",lng);
                    db=FirebaseFirestore.getInstance();
                    db.collection("Client")
                            .document(email).update(clientMap);
                    updateBookings();
                    profilePhoto.setAlpha((float) 1);
                    profilePhoto.setOnClickListener(null);
                    profileNameTv.setEnabled(false);
                    profilePhoneTv.setEnabled(false);
                    FAB_CLICKED_FLAG=0;
                    profileAddressTv.setOnClickListener(null);
                    settingsFab.setImageResource(R.drawable.settings_icon);
                }
                break;
            case R.id.profile_address:
                Intent intent1=new Intent(getContext(),MapsActivity.class);
                startActivityForResult(intent1,MAPS_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.client_photo:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(
                        Intent.createChooser(
                                intent,
                                "Select Image from here..."),
                        PICK_IMAGE_REQUEST);
                break;
        }
    }
}