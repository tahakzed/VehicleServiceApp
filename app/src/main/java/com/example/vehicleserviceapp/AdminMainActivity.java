package com.example.vehicleserviceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity implements View.OnClickListener {
    MaterialToolbar topAppBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View navHeader;
    String email="adminuser@gmail.com",name,phone,serviceStationName;
    double lat,lng;
    private AdminViewModel adminViewModel;
    NavHostFragment navHost;
    NavController navController;
    List<String> bookingIDs;
    List<String> reviewsList;
    long chargesCar,chargesBike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        initAdmin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adminViewModel=new ViewModelProvider(this).get(AdminViewModel.class);
        adminViewModel.getAdminDataWithEmail(email).observe(this, new Observer<List<Admin>>() {
            @Override
            public void onChanged(List<Admin> admins) {
                Admin admin=admins.get(0);
                name=admin.getName();
                email=admin.getEmail();
                phone=admin.getPhone();
                serviceStationName=admin.getServiceStationName();
                lat=admin.getLat();
                lng=admin.getLng();
                reviewsList=admin.getReviews();
                chargesBike=admin.getChargesBike();
                chargesCar=admin.getChargesCar();
                populateNavHeader(navHeader);
                bookingIDs=admin.getBookings();
                adminViewModel.getBookingsDataWithIds(bookingIDs).observe(AdminMainActivity.this, new Observer<List<Booking>>() {
                    @Override
                    public void onChanged(List<Booking> bookings) {
                        for(Booking b: bookings) {
                            if(b.getStatus().equals("Cancelled"))
                            {notifyOnBookingCanceled(b);
                            FirebaseFirestore db=FirebaseFirestore.getInstance();
                                db.collection("Bookings")
                                        .document(b.getBookingID()).delete();
                                bookingIDs.remove(b.getBookingID());
                                Map<String,Object> aMap=new HashMap<>();
                                aMap.put("Bookings",bookingIDs);
                                db.collection("Admin").document(email)
                                        .update(aMap);
                            }
                            else if(b.getStatus().equals("Pending"))
                                notifyOnNewBookingRequest(b);

                        }
                    }
                });
            }
        });
    }


    private void notifyOnNewBookingRequest(Booking booking){
        createNotificationChannel(booking.getBookingID());
        Intent intent = new Intent(getApplicationContext(), ClientBookingFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),"admin"+booking.getBookingID())
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle("New Booking Request!")
                .setContentText("You got a new booking from "+booking.getClientName())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void notifyOnBookingCanceled(Booking booking){
        createNotificationChannel(booking.getBookingID());
        Intent intent = new Intent(getApplicationContext(), ClientBookingFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),"admin"+booking.getBookingID())
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle("Booking Canceled!")
                .setContentText("You client: "+booking.getClientName()+" canceled his booking with you."+"\n" +
                        "Booking Data: "+booking.getDate()+"\n" +
                        "Booking Time: "+booking.getTime())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
    private void populateNavHeader(View view){
        TextView profileName=view.findViewById(R.id.profile_name);
        TextView profileEmail=view.findViewById(R.id.profile_email);
        profileEmail.setText(email);
        profileName.setText(name);

    }
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,drawerLayout);
    }
    public void initAdmin(){
        navigationView=findViewById(R.id.admin_nav_view);
        navHeader=navigationView.getHeaderView(0);
        drawerLayout=findViewById(R.id.admin_drawer_layout);
        topAppBar=findViewById(R.id.admin_topAppBar);
        setSupportActionBar(topAppBar);
        navHost=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.admin_placeholder);
        navController=navHost.getNavController();
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout);
        NavigationUI.setupWithNavController(navigationView,navController);
        navigationView.getHeaderView(0);
        navHeader.setOnClickListener(this);
    }
    private void createNotificationChannel(String bookingId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "admin"+bookingId;
            String description = "Notification channel for admin"+bookingId;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("admin"+bookingId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onClick(View v) {
        drawerLayout.closeDrawer(GravityCompat.START);
        Bundle bundle=new Bundle();
        bundle.putString("name",name);
        bundle.putString("email",email);
        bundle.putString("phone",phone);
        bundle.putDouble("lat",lat);
        bundle.putDouble("lng",lng);
        bundle.putLong("chargesCar",chargesCar);
        bundle.putLong("chargesBike",chargesBike);
        bundle.putStringArrayList("bookings",(ArrayList<String>) bookingIDs);
        bundle.putStringArrayList("reviews",(ArrayList<String>) reviewsList);
        bundle.putString("serviceStationName",serviceStationName);
        AdminHomeFragmentDirections.ActionAdminHomeFragmentToAdminProfileFragment action=
                AdminHomeFragmentDirections.actionAdminHomeFragmentToAdminProfileFragment(bundle).setAdminProfileData(bundle);
        navController.navigate(action);
    }
}