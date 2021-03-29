package com.example.vehicleserviceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;


public class AdminBookingRecyclerViewAdapter extends RecyclerView.Adapter<AdminBookingRecyclerViewAdapter.ViewHolder> {
    private List<Booking> bookings;
    private OnNoteListener onNoteListener;

    public AdminBookingRecyclerViewAdapter(List<Booking> bookings, OnNoteListener onNoteListener) {
        this.bookings=bookings;
        this.onNoteListener=onNoteListener;

    }

    @NonNull
    @Override
    public AdminBookingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_booking_item, parent, false);
        return new ViewHolder(view,onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookingRecyclerViewAdapter.ViewHolder holder, int position) {
        if (bookings != null) {
            Booking booking=bookings.get(position);
            holder.getClientNameTv().setText(booking.getClientName());
            holder.getTimeTv().setText("Time: "+booking.getTime());
            holder.getDateTv().setText("Date: "+booking.getDate());
            holder.getVehicleName().setText("Vehicle name: "+booking.getVehicleName());
            holder.getVehicleType().setText("Vehicle type: "+booking.getVehicleType());
            holder.getStatusTv().setText("Status: "+booking.getStatus());
        }
    }


    @Override
    public int getItemCount() {
        if (bookings != null) return bookings.size();
        return 0;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView clientNameTv;
        private TextView timeTv;
        private TextView dateTv;
        private TextView statusTv;
        private TextView vehicleName;
        private TextView vehicleType;
        private OnNoteListener onNoteListener;
        public ViewHolder(View view, OnNoteListener onNoteListener) {
            super(view);
            this.clientNameTv=view.findViewById(R.id.admin_booking_client_name);
            this.timeTv=view.findViewById(R.id.admin_booking_time);
            this.dateTv=view.findViewById(R.id.admin_booking_date);
            this.statusTv=view.findViewById(R.id.admin_booking_status);
            this.vehicleName=view.findViewById(R.id.admin_vehicle_name);
            this.vehicleType=view.findViewById(R.id.admin_vehicle_type);
            this.onNoteListener=onNoteListener;
            itemView.setOnClickListener(this);
        }

        public TextView getDateTv() {
            return dateTv;
        }

        public TextView getClientNameTv() {
            return clientNameTv;
        }

        public TextView getStatusTv() {
            return statusTv;
        }

        public TextView getTimeTv() {
            return timeTv;
        }

        public TextView getVehicleName() {
            return vehicleName;
        }

        public TextView getVehicleType() {
            return vehicleType;
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition(),v);

        }
    }
}
