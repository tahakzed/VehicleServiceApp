package com.example.vehicleserviceapp;

import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.ContentValues.TAG;


public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder>{
    private List<String> vehicleList;
    private OnNoteListener onNoteListener;
    public VehicleAdapter(List<String> vehicleList,OnNoteListener onNoteListener){
        this.vehicleList=vehicleList;
        this.onNoteListener=onNoteListener;
    }

    @NonNull
    @Override
    public VehicleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehicle_rv_item,parent,false);
        return new ViewHolder(view,onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.ViewHolder holder, int position) {

        if(vehicleList!=null){
            String[] arr=vehicleList.get(position).split(";");
            holder.getvNameTv().setText(arr[0]);
            if(arr[1].equals("Car"))
                holder.getvTypeImg().setImageResource(R.drawable.car_icon);
            else if(arr[1].equals("Bike"))
                holder.getvTypeImg().setImageResource(R.drawable.bike_icon);
        }
    }

    @Override
    public int getItemCount() {
        if(vehicleList!=null) return vehicleList.size();
        return 0;
    }

    public void setVehicleList(List<String> vehicleList){
        this.vehicleList=vehicleList;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private TextView vNameTv;
        private ImageView vTypeImg;
        private OnNoteListener onNoteListener;
        public ViewHolder(View view, OnNoteListener onNoteListener){
            super(view);
            vNameTv=view.findViewById(R.id.vname_vlist);
            vTypeImg=view.findViewById(R.id.vtype_img);
            this.onNoteListener=onNoteListener;
            itemView.setOnLongClickListener(this);
        }

        public ImageView getvTypeImg() {
            return vTypeImg;
        }

        public TextView getvNameTv() {
            return vNameTv;
        }

        @Override
        public boolean onLongClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition(),v);
            return true;
        }
    }
}
