package com.example.vehicleserviceapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AdminBookingInfoFragment extends Fragment implements View.OnClickListener {

    private TextView clientNameTv;
    private TextView dateTv;
    private TextView timeTv;
    private TextView statusTv;
    private TextView vehicleNameTv;
    private TextView addressTv;
    private Button mapBtn;
    private Button phoneBtn;
    private Button emailBtn;
    private ImageView vehicleIcon;
    private ExtendedFloatingActionButton paymentExFab;
    private View view;
    private String bookingId,clientName,clientEmail,date,time,status,address,vehicleName,vehicleType,clientPhone,paymentDate,paymentTime;
    private double lat,lng;
    private long charges,paymentTip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_admin_booking_info, container, false);
        init();
        getData();
        setData();
        return view;
    }
    private void init(){
        clientNameTv=view.findViewById(R.id.booking_info_client_name);
        dateTv=view.findViewById(R.id.booking_info_date);
        timeTv=view.findViewById(R.id.booking_info_time);
        statusTv=view.findViewById(R.id.booking_info_status);
        vehicleNameTv=view.findViewById(R.id.booking_info_vehicle);
        addressTv=view.findViewById(R.id.booking_info_client_address);
        mapBtn=view.findViewById(R.id.booking_info_client_location_mapbtn);
        phoneBtn=view.findViewById(R.id.booking_info_client_phonebtn);
        emailBtn=view.findViewById(R.id.booking_info_client_emailbtn);
        vehicleIcon=view.findViewById(R.id.booking_info_vehicle_type_icon);
        paymentExFab=view.findViewById(R.id.booking_info_payment_btn);
        paymentExFab.setOnClickListener(this);
        mapBtn.setOnClickListener(this);
        phoneBtn.setOnClickListener(this);
        emailBtn.setOnClickListener(this);
    }
    private Address getAddress(double lat, double lng){
        Geocoder geo=new Geocoder(getContext(), Locale.getDefault());
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
    private void getData(){
        Bundle args=AdminBookingInfoFragmentArgs.fromBundle(getArguments()).getBookingInfoBundle();
        clientName=args.getString("clientName");
        clientEmail=args.getString("clientEmail");
        clientPhone=args.getString("clientPhone");
        date=args.getString("date");
        time=args.getString("time");
        status=args.getString("status");
        lat=args.getDouble("lat");
        lng=args.getDouble("lng");
        vehicleName=args.getString("vehicleName");
        vehicleType=args.getString("vehicleType");
        charges=args.getLong("paymentCharges");
        bookingId=args.getString("bookingId");
        address=getAddress(lat,lng).getAddressLine(0);
    }
    private void setData(){
        clientNameTv.setText(clientName);
        emailBtn.setText(clientEmail);
        phoneBtn.setText(clientPhone);
        statusTv.setText(status);
        timeTv.setText(time);
        dateTv.setText(date);
        vehicleNameTv.setText(vehicleName);
        if(vehicleType.equals("Car"))
            vehicleIcon.setImageResource(R.drawable.car_icon2);
        else if(vehicleType.equals("Bike"))
            vehicleIcon.setImageResource(R.drawable.bike_icon2);
        addressTv.setText(address);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.booking_info_client_phonebtn:
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:"+clientPhone));
                startActivity(phoneIntent);
                break;
            case R.id.booking_info_client_location_mapbtn:
                String uri = String.format(Locale.getDefault(), "http://maps.google.com/maps?q=loc:%f,%f", lat,lng);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                break;
            case R.id.booking_info_client_emailbtn:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{clientEmail});
                Intent mailer = Intent.createChooser(emailIntent, null);
                startActivity(mailer);
                break;
            case R.id.booking_info_payment_btn:
                addPayment();
                break;
        }
    }
    private void addPayment(){
        Calendar cal=Calendar.getInstance();
        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                paymentDate=dayOfMonth+"/"+month+"/"+year;
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        paymentTime=hourOfDay+":"+minute;
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        View dialogView=LayoutInflater.from(getContext())
                                .inflate(R.layout.payment_dialog_view,null,false);
                        EditText paymentTipEt =dialogView.findViewById(R.id.payment_dialog_tip);

                        builder.setTitle("Add tip if any");
                        builder.setView(dialogView);
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String paymentTipStr=paymentTipEt.getText().toString();
                                if(paymentTipStr==null || paymentTipStr.equals(""))
                                    paymentTipStr="0";
                                paymentTip=Long.parseLong(paymentTipStr);
                                Map<String,Object> paymentMap=new HashMap<>();
                                paymentMap.put("Payment Date",paymentDate);
                                paymentMap.put("Payment Time",paymentTime);
                                paymentMap.put("Payment Tip",paymentTip);
                                paymentMap.put("Payment Status","Paid");
                                paymentMap.put("Status","Complete");
                                FirebaseFirestore.getInstance().collection("Bookings")
                                        .document(bookingId).update(paymentMap);
                            }
                        });
                        builder.show();
                    }
                }, cal.HOUR_OF_DAY, cal.MINUTE, false).show();
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}