package com.example.vehicleserviceapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class ClientProfileFragment extends Fragment {
    private TextView profileNameTv;
    private TextView profileEmailTv;
    private TextView profilePhoneTv;
    private TextView profileAddressTv;
    private TextView profileBookingCountTv;
    private MyViewModel myViewModel;
    private Bundle client_data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_client_profile, container, false);
        profileNameTv=view.findViewById(R.id.profile_name);
        profileEmailTv=view.findViewById(R.id.profile_email);
        profilePhoneTv=view.findViewById(R.id.profile_phone);
        profileAddressTv=view.findViewById(R.id.profile_address);
        profileBookingCountTv=view.findViewById(R.id.profile_bookings_count);
        client_data=ClientProfileFragmentArgs.fromBundle(getArguments()).getClientData();
        return view;
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
    public void onResume() {
        super.onResume();
//        String email=client_data.getString("client email");
//        myViewModel.getClientDataWithEmail(email).observe(getViewLifecycleOwner(), new Observer<List<Client>>() {
//            @Override
//            public void onChanged(List<Client> clients) {
//                Client client=clients.get(0);
//                String name=client.getName();
//                String email=client.getEmail();
//                String phone=client.getPhone();
//                String address=getAddress(client.getLat(),client.getLng()).getAddressLine(0);
//                int bookingsCount=client.getBookings().size();
//                profileNameTv.setText(name);
//                profileEmailTv.setText(email);
//                profileAddressTv.setText(address);
//                profilePhoneTv.setText(phone);
//                profileAddressTv.setText(bookingsCount);
//            }
//        });
    }
}