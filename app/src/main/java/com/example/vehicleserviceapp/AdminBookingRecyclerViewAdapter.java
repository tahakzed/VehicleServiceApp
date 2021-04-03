package com.example.vehicleserviceapp;

import android.content.Context;
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
    private Context context;

    public AdminBookingRecyclerViewAdapter(List<Booking> bookings, OnNoteListener onNoteListener,Context context) {
        this.bookings=bookings;
        this.onNoteListener=onNoteListener;
        this.context=context;

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
            if(booking.getStatus().equals("Pending"))
                holder.getStatusTv().setTextColor(context.getResources().getColor(R.color.pending_color));
            else if(booking.getStatus().equals("In-Progress"))
                holder.getStatusTv().setTextColor(context.getResources().getColor(R.color.in_progress_color));
            else if(booking.getStatus().equals("Completed"))
                holder.getStatusTv().setTextColor(context.getResources().getColor(R.color.mid_blue));
            String bookingId=booking.getBookingID();
            holder.getBookingIdTv().setText("Booking Id # "+bookingId.substring(0,3)+bookingId.substring(bookingId.length()-4,bookingId.length()));
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
        private TextView bookingIdTv;
        private OnNoteListener onNoteListener;
        public ViewHolder(View view, OnNoteListener onNoteListener) {
            super(view);
            this.clientNameTv=view.findViewById(R.id.admin_booking_client_name);
            this.timeTv=view.findViewById(R.id.admin_booking_time);
            this.dateTv=view.findViewById(R.id.admin_booking_date);
            this.statusTv=view.findViewById(R.id.admin_booking_status);
            this.vehicleName=view.findViewById(R.id.admin_vehicle_name);
            this.vehicleType=view.findViewById(R.id.admin_vehicle_type);
            this.bookingIdTv=view.findViewById(R.id.admin_booking_id);
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

        public TextView getBookingIdTv() {
            return bookingIdTv;
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition(),v);

        }
    }
}
