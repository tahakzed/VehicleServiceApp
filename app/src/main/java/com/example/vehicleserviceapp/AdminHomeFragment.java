package com.example.vehicleserviceapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;


import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AdminHomeFragment extends Fragment implements OnNoteListener {

    RecyclerView bookingRecyclerView;
    AdminBookingRecyclerViewAdapter adapter;
    AdminViewModel adminViewModel;
    List<String> bookingIDs;
    List<Booking> bookings;
    String adminEmail;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_admin_home, container, false);
        init();
        return view;
    }
    private String getAdminEmailFromSharedPreferences(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPreferences.getString("Email","");
    }
    private void init(){
        adminEmail=getAdminEmailFromSharedPreferences();
        bookingIDs=new ArrayList<>();
        bookings=new ArrayList<>();
        bookingRecyclerView=view.findViewById(R.id.admin_booking_list);
        adapter=new AdminBookingRecyclerViewAdapter(bookings,this,getContext());
        adminViewModel=new ViewModelProvider(getActivity()).get(AdminViewModel.class);
        adminViewModel.getAdminDataWithEmail(adminEmail).observe(getViewLifecycleOwner(), new Observer<List<Admin>>() {
            @Override
            public void onChanged(List<Admin> admins) {
                Admin admin=admins.get(0);
                bookingIDs=admin.getBookings();
                adminViewModel.getBookingsDataWithIds(bookingIDs).observe(getViewLifecycleOwner(), new Observer<List<Booking>>() {
                    @Override
                    public void onChanged(List<Booking> bs){
                        if(bookings==null)
                            bookings=new ArrayList<>();
                        else
                            bookings.clear();
                        for(Booking b : bs)
                            if(b.getStatus().equals("In-Progress"))
                                bookings.add(b);
                        adapter.setBookings(bookings);
                    }
                });
            }
        });

        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onNoteClick(int position, View view) {
        Booking booking=bookings.get(position);
        String clientName=booking.getClientName();
        String date=booking.getDate();
        String time=booking.getTime();
        String status=booking.getStatus();
        String vehicleName=booking.getVehicleName();
        String vehicleType=booking.getVehicleType();
        double lat=booking.getClientLat();
        double lng=booking.getClientLng();
        long charges=booking.getPaymentCharges();
        String clientPhone=booking.getClientPhone();
        String bookingId=booking.getBookingID();
        String clientEmail=booking.getClientEmail();
        String clientImageId=booking.getClientImageId();
        Bundle bundle=new Bundle();
        bundle.putString("clientName",clientName);
        bundle.putString("clientEmail",clientEmail);
        bundle.putString("clientPhone",clientPhone);
        bundle.putString("date",date);
        bundle.putString("time",time);
        bundle.putString("vehicleName",vehicleName);
        bundle.putString("vehicleType",vehicleType);
        bundle.putLong("paymentCharges",charges);
        bundle.putString("bookingId",bookingId);
        bundle.putString("status",status);
        bundle.putDouble("lat",lat);
        bundle.putDouble("lng",lng);
        if(!clientImageId.equals(""))
            bundle.putString("clientImageId",clientImageId);
        AdminHomeFragmentDirections.ActionAdminHomeFragmentToAdminBookingInfoFragment action=
                AdminHomeFragmentDirections.actionAdminHomeFragmentToAdminBookingInfoFragment().setBookingInfoBundle(bundle);
        Navigation.findNavController(view).navigate(action);
    }
}