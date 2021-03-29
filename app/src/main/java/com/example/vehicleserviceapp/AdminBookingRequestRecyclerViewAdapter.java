package com.example.vehicleserviceapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;


public class AdminBookingRequestRecyclerViewAdapter extends RecyclerView.Adapter<AdminBookingRequestRecyclerViewAdapter.ViewHolder> {
    private List<Booking> bookings;
    private OnButtonClickListener onButtonClickListener;

    public AdminBookingRequestRecyclerViewAdapter(List<Booking> bookings, OnButtonClickListener onButtonClickListener) {
        this.bookings=bookings;
        this.onButtonClickListener=onButtonClickListener;

    }

    @NonNull
    @Override
    public AdminBookingRequestRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_booking_request_item, parent, false);
        return new ViewHolder(view,onButtonClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookingRequestRecyclerViewAdapter.ViewHolder holder, int position) {
        if (bookings != null) {
            Booking booking=bookings.get(position);
            holder.getClientNameTv().setText(booking.getClientName());
            holder.getTimeTv().setText("Time: "+booking.getTime());
            holder.getDateTv().setText("Date: "+booking.getDate());
            holder.getVehicleName().setText("Vehicle name: "+booking.getVehicleName());
            holder.getVehicleType().setText("Vehicle type: "+booking.getVehicleType());
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
        private TextView vehicleName;
        private TextView vehicleType;
        private OnButtonClickListener onButtonClickListener;
        private Button declineBtn;
        private Button acceptBtn;
        public ViewHolder(View view, OnButtonClickListener onButtonClickListener) {
            super(view);
            this.clientNameTv=view.findViewById(R.id.admin_request_client_name);
            this.timeTv=view.findViewById(R.id.admin_request_time);
            this.dateTv=view.findViewById(R.id.admin_request_date);
            this.declineBtn=view.findViewById(R.id.admin_request_decline_button);
            this.acceptBtn=view.findViewById(R.id.admin_request_accept_button);
            this.vehicleName=view.findViewById(R.id.admin_request_vehicle_name);
            this.vehicleType=view.findViewById(R.id.admin_request_vehicle_type);
            this.onButtonClickListener=onButtonClickListener;
            declineBtn.setOnClickListener(this);
            acceptBtn.setOnClickListener(this);
        }

        public TextView getDateTv() {
            return dateTv;
        }

        public TextView getClientNameTv() {
            return clientNameTv;
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
            onButtonClickListener.onButtonClick(getAdapterPosition(),v.getId(),v);

        }
    }
}
