package com.example.vehicleserviceapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    FloatingActionButton fab,phoneFab,emailFab,locFab;
    ExtendedFloatingActionButton bookingFab;
    RecyclerView recyclerView;
    String name,email,phone,serviceStationName,clientEmail;
    private TextView serviceStationTv,ratingBarTv,numOfReviewsTv;
    private RatingBar ratingBar;
    double lat,lng;
    int charges;
    double clientLat,clientLng;
    List<String> reviews_list;
    String date,time;
    ReviewsRecyclerAdapter recyclerAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_booking, container, false);

        //textviews
        serviceStationTv=view.findViewById(R.id.service_s_profile_name);
        ratingBarTv=view.findViewById(R.id.rating_bar_textview);
        numOfReviewsTv=view.findViewById(R.id.reviews_num_tv);
        ratingBar=view.findViewById(R.id.rating_bar);
        //recycler_view
        recyclerView=view.findViewById(R.id.reviews_recycler_view);
        //fab
        fab=view.findViewById(R.id.fab);

        phoneFab=view.findViewById(R.id.phone_fab);
        emailFab=view.findViewById(R.id.email_fab);
        locFab=view.findViewById(R.id.loc_fab);
        bookingFab=view.findViewById(R.id.booking_fab);
        fab.setOnClickListener(this);
        phoneFab.setOnClickListener(this);
        emailFab.setOnClickListener(this);
        locFab.setOnClickListener(this);
        bookingFab.setOnClickListener(this);
        getData();
        setData();
        recyclerAdapter=new ReviewsRecyclerAdapter(reviews_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerAdapter);
        return view;
    }

    private void getData(){
        Bundle args=BookingFragmentArgs.fromBundle(getArguments()).getArgmnts();
        clientEmail=args.getString("client email");
        name=args.getString("name");
        email=args.getString("email");
        phone=args.getString("phone");
        serviceStationName=args.getString("service station");
        lat=args.getDouble("lat");
        lng=args.getDouble("lng");
        reviews_list=args.getStringArrayList("reviews");
        charges=args.getInt("charges");

    }

    private void setData(){
        serviceStationTv.setText(serviceStationName);
        numOfReviewsTv.setText("Reviews("+reviews_list.size()+")");
        float rate=calculateRating();
        ratingBar.setRating(rate);
        ratingBarTv.setText("("+rate+"),");
    }
    private void bookService(){
        Calendar cal=Calendar.getInstance();
        DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(),this, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private float calculateRating(){
        float star5=0,star4=0,star3=0,star2=0,star1=0;
        for(String str : reviews_list)
        {
            String[] arr=str.split(";");
            if(arr[1].equals("5.0"))
                star5++;
            else if(arr[1].equals("4.0"))
                star4++;
            else if(arr[1].equals("3.0"))
                star3++;
            else if(arr[1].equals("2.0"))
                star4++;
            else if(arr[1].equals("1.0"))
                star1++;
        }

    return (5*star5+4*star4+3*star3+2*star2+1*star1)/reviews_list.size();
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.fab:
                fab.setExpanded(!fab.isExpanded());
                break;
            case R.id.phone_fab:
                fab.setExpanded(false);
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:"+phone));
                startActivity(phoneIntent);
                break;
            case R.id.email_fab:
                fab.setExpanded(false);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{email});
                Intent mailer = Intent.createChooser(emailIntent, null);
                startActivity(mailer);
                break;
            case R.id.loc_fab:
                fab.setExpanded(false);
                String uri = String.format(Locale.getDefault(), "http://maps.google.com/maps?q=loc:%f,%f", lat,lng);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                break;
            case R.id.booking_fab:
                fab.setExpanded(false);
                bookService();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date=dayOfMonth+"/"+month+"/"+year;
        TimePickerDialog timePickerDialog=new TimePickerDialog(getContext(),this,Calendar.HOUR_OF_DAY,Calendar.MINUTE,false);
        timePickerDialog.show();
    }
    private String[] getVehicleNames(List<String> v){
        List<String> temp=new ArrayList<>();
        for(String str : v){
            temp.add(str.split(";")[0]);
        }
        String[] arr=new String[temp.size()];
        for(int i=0;i<arr.length;i++){
            arr[i]=temp.get(i);
        }
        return arr;
    }
    private String findVType(List<String> v,String name){
        String type="";
        for(String str : v){
            if(str.contains(name)){
                type=str.substring(str.indexOf(";")+1);
                return type;
            }
        }
        return null;
    }
    String vname;
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time=hourOfDay+":"+minute;
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.document("Client/"+clientEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    DocumentSnapshot doc=task.getResult();
                    List<String> vv=(List<String>) doc.get("Vehicles");
                    String[] vnames=getVehicleNames(vv);
                    String cphone=doc.get("Phone").toString();
                    new AlertDialog.Builder(getContext())
                            .setSingleChoiceItems(vnames, 0, null)
                            .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    vname=vnames[((AlertDialog)dialog).getListView().getCheckedItemPosition()];
                                    clientLat=(Double)doc.get("Lat");
                                    clientLng=(Double)doc.get("Lng");
                                    Map<String,Object> dbData=new HashMap<>();
                                    dbData.put("Lat",clientLat);
                                    dbData.put("Lng",clientLng);
                                    dbData.put("Admin Email",email);
                                    dbData.put("Client Name",doc.get("Name"));
                                    dbData.put("Payment Status","Unpaid");
                                    dbData.put("Payment Charges",charges);
                                    dbData.put("Payment Date","NaN");
                                    dbData.put("Payment Time","NaN");
                                    dbData.put("Payment Tip",0);
                                    dbData.put("Status","Pending");
                                    dbData.put("Date",date);
                                    dbData.put("Time",time);
                                    dbData.put("Vehicle Name",vname);
                                    dbData.put("Vehicle Type",findVType(vv,vname));
                                    dbData.put("Client Phone",cphone);
                                    dbData.put("Client Email",clientEmail);
                                    dbData.put("Service Station",serviceStationName);
                                    String bookingID= db.collection("Bookings").document().getId();
                                    dbData.put("Booking-ID",bookingID);
                                    db.collection("Bookings").document(bookingID).set(dbData);
                                    Map<String,Object> adminMap=new HashMap<>();
                                    List<String> b=new ArrayList<>();
                                    b.add(bookingID);
                                    adminMap.put("Bookings",b);
                                    db.document("Admin/"+email).update(adminMap);
                                    Map<String,Object> clientMap=new HashMap<>();
                                    List<String> cb=new ArrayList<>();
                                    cb.add(bookingID);
                                    clientMap.put("Bookings",cb);
                                    db.document("Client/"+clientEmail).update(clientMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(),"BOOKED!",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .show();


                }
                else{
                    Toast.makeText(getContext(),"Booking Failed! Please try again later",Toast.LENGTH_SHORT).show();
                }
            }
        });
//
    }
}