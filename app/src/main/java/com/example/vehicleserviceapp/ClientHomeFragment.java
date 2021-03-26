package com.example.vehicleserviceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ClientHomeFragment extends Fragment implements OnNoteListener{

    MyViewModel myViewModel;
    RecyclerView recyclerView;
    List<Admin> adminList;
    MyAdapter adapter;
    String clientEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_client_home, container, false);
        myViewModel= new ViewModelProvider(getActivity()).get(MyViewModel.class);
        recyclerView=view.findViewById(R.id.service_station_list);
        clientEmail="tahakzed@gmail.com";
        adapter=new MyAdapter(adminList,getContext(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myViewModel.getAllAdmins().observe(getViewLifecycleOwner(), new Observer<List<Admin>>() {
            @Override
            public void onChanged(List<Admin> admins) {
                adminList=admins;
                adapter.setTasks(adminList);

            }
        });
    }

    @Override
    public void onNoteClick(int position,View view) {

        Admin current=adminList.get(position);
        String name=current.getName();
        String email=current.getEmail();
        String phone=current.getPhone();
        String serviceStationName=current.getServiceStationName();
        double lat=current.getLat();
        double lng=current.getLng();
        int charges=current.getCharges();
        Bundle bundle=new Bundle();
        bundle.putString("client email",clientEmail);
        bundle.putString("name",name);
        bundle.putString("email",email);
        bundle.putString("phone",phone);
        bundle.putString("service station",serviceStationName);
        bundle.putDouble("lat",lat);
        bundle.putDouble("lng",lng);
        bundle.putInt("charges",charges);
        bundle.putStringArrayList("reviews",(ArrayList<String>) current.getReviews());
        ClientHomeFragmentDirections.ActionClientHomeFragmentToBookingFragment action=
                ClientHomeFragmentDirections.actionClientHomeFragmentToBookingFragment().setArgmnts(bundle);
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onStop() {
        super.onStop();
        myViewModel.getAllAdmins().removeObservers(getViewLifecycleOwner());
    }
}