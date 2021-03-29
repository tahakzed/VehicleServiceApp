package com.example.vehicleserviceapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;


public class ClientBookingRecyclerViewAdapter extends RecyclerView.Adapter<ClientBookingRecyclerViewAdapter.ViewHolder> {
    private List<Booking> bookings;
    private OnNoteListener onNoteListener;

    public ClientBookingRecyclerViewAdapter(List<Booking> bookings, OnNoteListener onNoteListener) {
        this.bookings=bookings;
        this.onNoteListener=onNoteListener;

    }

    @NonNull
    @Override
    public ClientBookingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_item, parent, false);
        return new ViewHolder(view,onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientBookingRecyclerViewAdapter.ViewHolder holder, int position) {
        if (bookings != null) {
            Booking booking=bookings.get(position);
            holder.getServiceStationNameTv().setText(booking.getServiceStationName());
            holder.getTimeTv().setText("Time: "+booking.getTime());
            holder.getDateTv().setText("Date: "+booking.getDate());
            holder.getVehicleNameTv().setText("Vehicle name: "+booking.getVehicleName());
            holder.getVehicleTypeTv().setText("Vehicle type: "+booking.getVehicleType());
            holder.getStatusTv().setText("Status: "+booking.getStatus());
            Log.d("BOOKING ADAPTEr", "onBindViewHolder: "+booking.getVehicleName());
        }
    }


    @Override
    public int getItemCount() {
        if (bookings != null) return bookings.size();
        return 0;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings=bookings;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private TextView serviceStationNameTv;
        private TextView timeTv;
        private TextView dateTv;
        private TextView statusTv;
        private TextView vehicleNameTv;
        private TextView vehicleTypeTv;
        private OnNoteListener onNoteListener;
        public ViewHolder(View view, OnNoteListener onNoteListener) {
            super(view);
            this.serviceStationNameTv=view.findViewById(R.id.booking_service_station);
            this.timeTv=view.findViewById(R.id.client_booking_time);
            this.dateTv=view.findViewById(R.id.client_booking_date);
            this.statusTv=view.findViewById(R.id.client_booking_status);
            this.vehicleNameTv=view.findViewById(R.id.client_vehicle_name);
            this.vehicleTypeTv=view.findViewById(R.id.client_vehicle_type);
            this.onNoteListener=onNoteListener;
            itemView.setOnLongClickListener(this);
        }

        public TextView getDateTv() {
            return dateTv;
        }

        public TextView getServiceStationNameTv() {
            return serviceStationNameTv;
        }

        public TextView getStatusTv() {
            return statusTv;
        }

        public TextView getTimeTv() {
            return timeTv;
        }

        public TextView getVehicleNameTv() {
            return vehicleNameTv;
        }

        public TextView getVehicleTypeTv() {
            return vehicleTypeTv;
        }

        @Override
        public boolean onLongClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition(),v);
            return true;
        }
    }
}
