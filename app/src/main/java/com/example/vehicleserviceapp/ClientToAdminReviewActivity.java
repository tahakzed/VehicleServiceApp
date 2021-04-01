package com.example.vehicleserviceapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientToAdminReviewActivity extends AppCompatActivity implements View.OnClickListener {
    RatingBar ratingBar;
    Button addReviewButton;
    Button doneButton;
    EditText reviewEt;
    String review="",clientName,bookingId;
    TextView reviewTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_to_admin_review);
        init();
        getData();
    }
    private void init(){
        ratingBar=findViewById(R.id.client_to_admin_rating_bar);
        addReviewButton=findViewById(R.id.client_to_admin_add_review_button);
        doneButton=findViewById(R.id.client_to_admin_done_btn);
        reviewEt=findViewById(R.id.client_to_admin_add_review_et);
        reviewTitle=findViewById(R.id.client_to_admin_review_title);
        addReviewButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
    }
    private void getData(){
        Intent intent=getIntent();
        clientName=intent.getStringExtra("clientName");
        bookingId=intent.getStringExtra("bookingId");
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.client_to_admin_add_review_button:
                if(reviewEt.getVisibility()==View.GONE)
                {reviewEt.setVisibility(View.VISIBLE);
                    addReviewButton.setText("X remove review");
                    addReviewButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));}
                else if(reviewEt.getVisibility()==View.VISIBLE)
                {reviewEt.setVisibility(View.GONE);
                    reviewEt.setText("");
                    addReviewButton.setText("+ add review");
                    addReviewButton.setTextColor(getResources().getColor(R.color.mid_blue));}

                break;
            case R.id.client_to_admin_done_btn:
                if(reviewEt.getVisibility()==View.VISIBLE)
                    review=reviewEt.getText().toString();
                float rating=ratingBar.getRating();
                String reviewString=clientName+";"+rating+";"+review;
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                db.collection("Bookings")
                        .document(bookingId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String adminEmail=documentSnapshot.get("Admin Email").toString();
                        String serviceStation=documentSnapshot.get("Service Station").toString();
                        reviewTitle.setText(serviceStation);
                        db.collection("Admin").document(adminEmail)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                List<String> reviewList=(List<String>)documentSnapshot.get("Reviews");
                                reviewList.add(reviewString);
                                Map<String,Object> adminMap=new HashMap<>();
                                adminMap.put("Reviews",reviewList);
                                db.collection("Admin").document(adminEmail).update(adminMap);
                                new AlertDialog.Builder(ClientToAdminReviewActivity.this)
                                        .setTitle("Success!")
                                        .setMessage("Review added!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }).setIcon(R.drawable.done_icon)
                                        .show();
                            }
                        }); //admin
                    }
                }); //booking
                break;
        }
    }
}