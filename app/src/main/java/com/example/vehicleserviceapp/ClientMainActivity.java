package com.example.vehicleserviceapp;

import androidx.annotation.NonNull;
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
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClientMainActivity extends AppCompatActivity{
    MaterialToolbar topAppBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View navHeader;
    String email="tahakzed@gmail.com",name,phone;
    double lat,lng;
    private MyViewModel myViewModel;
    NavHostFragment navHost;
    NavController navController;
    List<String> bookingIDs;
    int f=0;
    Bundle bundle=new Bundle();
    List<Booking> bookings=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);


       // Intent intent=getIntent();
        //email=intent.getStringExtra("Email");    //UNCOMMENT LATER
        //chooseUserType();
        bundle.putString("client email",email);
        initClient();

    }

    @Override
    protected void onResume() {
        super.onResume();
        myViewModel=new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getClientDataWithEmail(email).observe(this, new Observer<List<Client>>() {
            @Override
            public void onChanged(List<Client> clients) {
                Client client=clients.get(0);
                name=client.getName();
                email=client.getEmail();
                populateNavHeader(navHeader);
                bookingIDs=client.getBookings();
                myViewModel.getBookingsDataWithIds(bookingIDs).observe(ClientMainActivity.this, new Observer<List<Booking>>() {
                    @Override
                    public void onChanged(List<Booking> bs){
                        if(bookings.size()<=bs.size()){
                        for(int i=0;i<bookings.size();i++){

                            if(!bookings.get(i).getStatus().equals(bs.get(i).getStatus()))
                                notifyOnBookingChanged(bs.get(i));
                        }}
                        bookings=bs;
                    }
                });

            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        myViewModel.getClientDataWithEmail(email).removeObservers(this);
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,drawerLayout);
    }

    private void initClient(){
        createNotificationChannel();
        navigationView=findViewById(R.id.nav_view);
        navHeader=navigationView.getHeaderView(0);
        drawerLayout=findViewById(R.id.drawer_layout);
        topAppBar=findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        navHost=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.placeholder);
        navController=navHost.getNavController();
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout);
        NavigationUI.setupWithNavController(navigationView,navController);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "bookings";
            String description = "Notification channel for booking";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("bookings", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void notifyOnBookingChanged(Booking booking){
        Intent intent = new Intent(getApplicationContext(), ClientBookingFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),"bookings")
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle(booking.getServiceStationName())
                .setContentText("Your service status: "+booking.getStatus())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your service status: "+booking.getStatus()))
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

    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.vehicle_list:
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                VehicleFragment vehicleFragment=new VehicleFragment();
                vehicleFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder,vehicleFragment)
                        .commit();

                return true;
            case R.id.home:
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                ClientHomeFragment clientHomeFragment=new ClientHomeFragment();
                clientHomeFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder,clientHomeFragment)
                        .commit();

                return true;
            case R.id.booking_list:
                item.setChecked(true);
                drawerLayout.closeDrawer(GravityCompat.START);
                ClientBookingFragment clientBookingFragment=new ClientBookingFragment();
                clientBookingFragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.placeholder,clientBookingFragment)
                        .commit();
                return true;
        }
        return false;
    }*/
}
