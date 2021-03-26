package com.example.vehicleserviceapp;

import android.app.Application;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewsRecyclerAdapter extends RecyclerView.Adapter<ReviewsRecyclerAdapter.ViewHolder> {
    private List<String> reviewList;

    public ReviewsRecyclerAdapter (List<String> reviewList){
        this.reviewList=reviewList;

    }

    @NonNull
    @Override
    public ReviewsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_review_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsRecyclerAdapter.ViewHolder holder, int position) {
        if(reviewList!=null){
            String[] strArr=reviewList.get(position).split(";");
            if(reviewList.size()==0){
                return;
            }
            holder.getNameTv().setText(strArr[0]+",");    //[0]: client name
            holder.getRatingBar().setRating(Float.valueOf(strArr[1]));   //[1]: rating
            holder.getRatingTV().setText("("+strArr[1]+")");
            holder.getReviewTv().setText(strArr[2]); //[2]: client's review

        }
    }

    @Override
    public int getItemCount() {
        if(reviewList!=null) return reviewList.size();
        return 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTv;
        private TextView reviewTv;
        private RatingBar ratingBar;
        private TextView ratingTV;
        ViewHolder(View view){
            super(view);
            this.nameTv=view.findViewById(R.id.buyer_name);
            this.reviewTv=view.findViewById(R.id.buyer_review);
            this.ratingBar=view.findViewById(R.id.buyer_rating_bar);
            this.ratingTV=view.findViewById(R.id.buyer_rating_tv);
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }

        public TextView getNameTv() {
            return nameTv;
        }

        public TextView getReviewTv() {
            return reviewTv;
        }

        public TextView getRatingTV() {
            return ratingTV;
        }
    }
}
