package com.example.vehicleserviceapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminBookingRequestFragment extends Fragment implements OnButtonClickListener {

    RecyclerView bookingRecyclerView;
    AdminBookingRequestRecyclerViewAdapter adapter;
    AdminViewModel adminViewModel;
    List<String> bookingIDs;
    List<Booking> bookings;
    String adminEmail;
    View view;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_admin_booking_request, container, false);
        init();

        return view;
    }
    private void init(){
        db=FirebaseFirestore.getInstance();
        adminEmail="adminuser@gmail.com";
        bookingIDs=new ArrayList<>();

        bookingRecyclerView=view.findViewById(R.id.admin_booking_request_list);
        adapter=new AdminBookingRequestRecyclerViewAdapter(bookings,this);
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
                            if(b.getStatus().equals("Pending"))
                                bookings.add(b);
                        adapter.setBookings(bookings);
                    }
                });
            }
        });

        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingRecyclerView.setAdapter(adapter);
    }


    private void deleteBookingById(String id){
        db.collection("Admin").document(adminEmail)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> adminBookingId=(List<String>)documentSnapshot.get("Bookings");
                adminBookingId.remove(id);
                Map<String,Object> adminMap=new HashMap<>();
                adminMap.put("Bookings",adminBookingId);
                db.collection("Admin").document(adminEmail)
                        .update(adminMap);

            }
        });
    }

    @Override
    public void onButtonClick(int position, int id, View view) {
        Booking current=bookings.get(position);

        switch (id){
            case R.id.admin_request_accept_button:
                Map<String,Object> bookingsMap=new HashMap<>();
                bookingsMap.put("Status","In-Progress");
                db.collection("Bookings").document(current.getBookingID())
                        .update(bookingsMap);
                break;
            case R.id.admin_request_decline_button:
                Map<String,Object> bookingsMap2=new HashMap<>();
                bookingsMap2.put("Status","Declined");
                db.collection("Bookings").document(current.getBookingID())
                        .update(bookingsMap2);
                deleteBookingById(current.getBookingID());
                break;
        }
    }
}