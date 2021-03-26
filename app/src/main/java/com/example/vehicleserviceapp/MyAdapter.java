package com.example.vehicleserviceapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Admin> admins;
    private Context context;
    private OnNoteListener onNoteListener;
    public MyAdapter(List<Admin> admins,Context context,OnNoteListener onNoteListener){
        this.admins=admins;
        this.context=context;
        this.onNoteListener=onNoteListener;
    }
    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item,parent,false);
        return new ViewHolder(view,onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        if(admins!=null){
            Address address=getAddress(admins.get(position).getLat(),admins.get(position).getLng());
            holder.getServiceStationName().setText(admins.get(position).getServiceStationName());
            holder.getCityName().setText(address.getLocality()+", "+address.getCountryName());
            holder.getEmail().setText(admins.get(position).getEmail());

        }
    }
    private Address getAddress(double lat, double lng){
        Geocoder geo=new Geocoder(context, Locale.getDefault());
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
    public int getItemCount() {
        if(admins!=null) return admins.size();
        return 0;
    }
    public void setTasks(List<Admin> admins){
        this.admins=admins;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView serviceStationName;
        private TextView cityName;
        private TextView email;
        private OnNoteListener onNoteListener;
        public ViewHolder(View view,OnNoteListener onNoteListener){
            super(view);
            serviceStationName=view.findViewById(R.id.service_station_name);
            cityName=view.findViewById(R.id.city_name);
            email=view.findViewById(R.id.email_id_admin);
            itemView.setOnClickListener(this);
            this.onNoteListener=onNoteListener;
        }

        public TextView getCityName() {
            return cityName;
        }

        public TextView getEmail() {
            return email;
        }

        public TextView getServiceStationName() {
            return serviceStationName;
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition(),v);
        }
    }
}
