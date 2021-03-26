package com.example.vehicleserviceapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientBookingFragment extends Fragment implements OnNoteListener{
    RecyclerView bookingRecyclerView;
    ClientBookingRecyclerViewAdapter adapter;
    MyViewModel myViewModel;
    List<String> bookingIDs;
    List<Booking> bookings;
    String clientEmail;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_client_booking, container, false);
        init();

        return view;
    }
    private void init(){
       // clientEmail=getArguments().getString("client email");
        clientEmail="tahakzed@gmail.com";
        bookingIDs=new ArrayList<>();
        bookings=new ArrayList<>();
        bookingRecyclerView=view.findViewById(R.id.client_booking_list);
        adapter=new ClientBookingRecyclerViewAdapter(bookings,this);
        myViewModel=new ViewModelProvider(getActivity()).get(MyViewModel.class);
        myViewModel.getClientDataWithEmail(clientEmail).observe(getViewLifecycleOwner(), new Observer<List<Client>>() {
            @Override
            public void onChanged(List<Client> clients) {
                Client client=clients.get(0);
                bookingIDs=client.getBookings();
                myViewModel.getBookingsDataWithIds(bookingIDs).observe(getViewLifecycleOwner(), new Observer<List<Booking>>() {
                    @Override
                    public void onChanged(List<Booking> bs){
                        bookings=bs;
                        adapter.setBookings(bookings);
                    }
                });
            }
        });

        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingRecyclerView.setAdapter(adapter);
    }




    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onNoteClick(int position, View view) {
        Booking b=bookings.get(position);

        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking with "+b.getServiceStationName()+"?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String removeId=b.getBookingID();
                        List<String> tempIds=bookingIDs;
                        tempIds.remove(removeId);
                        FirebaseFirestore db= FirebaseFirestore.getInstance();
                        Map<String,Object> tempMap=new HashMap<>();
                        tempMap.put("Bookings",tempIds);
                        db.collection("Client").document(clientEmail)
                                .update(tempMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(),"Booking canceled!",Toast.LENGTH_SHORT).show();
                            }
                        });
                        db.collection("Bookings").document(removeId)
                                .delete();
                        String adminEmail=b.getAdminEmail();
                        db.collection("Admin").document(adminEmail)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                List<String> adminBookingId=(List<String>)documentSnapshot.get("Bookings");
                                adminBookingId.remove(removeId);
                                Map<String,Object> adminMap=new HashMap<>();
                                adminMap.put("Bookings",adminBookingId);
                                db.collection("Admin").document(adminEmail)
                                        .update(adminMap);

                            }
                        });
                        dialog.dismiss();
                    }

                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


}