package com.example.vehicleserviceapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class VehicleFragment extends Fragment implements View.OnClickListener,OnNoteListener {
    RecyclerView vListRecyclerView;
    ExtendedFloatingActionButton addVehicleBtn;
    VehicleAdapter vehicleAdapter;
    List<String> vList;
    MyViewModel myViewModel;
    String clientEmail;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_vehicle_list, container, false);
        init();
        return view;
    }
    private void init(){
        vListRecyclerView=view.findViewById(R.id.vehicle_list_rv);
        addVehicleBtn=view.findViewById(R.id.add_vehicle_button);
        addVehicleBtn.setOnClickListener(this);
        vList=new ArrayList<>();
        vehicleAdapter=new VehicleAdapter(vList,this);
        //clientEmail=getArguments().getString("client email");


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        clientEmail="tahakzed@gmail.com";
        myViewModel=new ViewModelProvider(getActivity()).get(MyViewModel.class);
        myViewModel.getClientDataWithEmail(clientEmail).observe(getViewLifecycleOwner(), new Observer<Client>() {
            @Override
            public void onChanged(Client c) {
                Client client=c;
                vList=client.getVehicleNameAndType();
                vehicleAdapter.setVehicleList(vList);
                Log.d(TAG,"VEHICLE LIST"+vList);
            }
        });
        vListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vListRecyclerView.setAdapter(vehicleAdapter);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.add_vehicle_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add a new vehicle");
                LayoutInflater layoutInflater=this.getLayoutInflater();
                View dialogView=layoutInflater.inflate(R.layout.add_vehicle_btn,null);
                EditText vname=dialogView.findViewById(R.id.vname_dialog);
                AutoCompleteTextView vtype=dialogView.findViewById(R.id.vtype_dialog);
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),R.layout.list_item,new String[]{"Car","Bike"});
                vtype.setAdapter(adapter);
                builder.setView(dialogView);
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String,Object> vMap=new HashMap<>();
                        vList.add(vname.getText().toString()+";"+vtype.getText().toString());
                        vMap.put("Vehicles",vList);
                        FirebaseFirestore.getInstance()
                                .document("Client/"+clientEmail)
                                .update(vMap)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Failed to add",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        myViewModel.getClientDataWithEmail(clientEmail).removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onNoteClick(int position, View view) {
        String vehicle=vList.get(position);
        String[] arr=vehicle.split(";");
        String vehicleName=arr[0];

        new AlertDialog.Builder(getContext())
                .setTitle("Delete Vehicle")
                .setMessage("Are you sure you want to delete your vehicle "+vehicleName+" from your vehicle list?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String,Object> vehicleMap=new HashMap<>();
                        List<String> tempList=vList;
                        tempList.remove(vehicle);
                        vehicleMap.put("Vehicles",tempList);
                        FirebaseFirestore.getInstance().collection("Client").document(clientEmail)
                                .update(vehicleMap);
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