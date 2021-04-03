package com.example.vehicleserviceapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientMainActivity extends AppCompatActivity implements View.OnClickListener {
    MaterialToolbar topAppBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View navHeader;
    String email,name,phone,imageId;
    double lat,lng;
    private MyViewModel myViewModel;
    NavHostFragment navHost;
    NavController navController;
    List<String> bookingIDs;
    int f=0;
    List<Booking> bookings=new ArrayList<>();
    Intent clientServiceIntent;
    private static int SERVICE_START_FLAG=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);
        Intent intent=getIntent();
        email=intent.getStringExtra("Email");
        initClient();

    }

    @Override
    protected void onResume() {
        super.onResume();
        myViewModel=new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getClientDataWithEmail(email).observe(this, new Observer<Client>() {
            @Override
            public void onChanged(Client c) {
                Client client=c;
                name=client.getName();
                email=client.getEmail();
                phone=client.getPhone();
                lat=client.getLat();
                lng=client.getLng();
                imageId=client.getImageId();
                populateNavHeader(navHeader);
                bookingIDs=client.getBookings();
                startClientBackgroundProcess();
                myViewModel.getBookingsDataWithIds(bookingIDs).observe(ClientMainActivity.this, new Observer<List<Booking>>() {
                    @Override
                    public void onChanged(List<Booking> bs){
                        bookings=bs;

                    }
                });

            }
        });
    }

    private void startClientBackgroundProcess(){
        clientServiceIntent=new Intent(getApplicationContext(),ClientBackgroundProcess.class);
        clientServiceIntent.putExtra("clientName",name);
        clientServiceIntent.putExtra("bookingIds",(Serializable) bookingIDs);
        clientServiceIntent.putExtra("clientEmail",email);
        clientServiceIntent.setAction("clientBackgroundProcess");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,clientServiceIntent,0);
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,0,10,pendingIntent);
    }

    @Override
    protected void onPause() {
        myViewModel.getClientDataWithEmail(email).removeObservers(this);
        super.onPause();

    }



    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,drawerLayout);
    }

    private void initClient(){

        navigationView=findViewById(R.id.nav_view);
        navHeader=navigationView.getHeaderView(0);
        drawerLayout=findViewById(R.id.drawer_layout);
        topAppBar=findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        navHost=(NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.placeholder);
        navController=navHost.getNavController();
        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout);
        NavigationUI.setupWithNavController(navigationView,navController);
        navigationView.getHeaderView(0);
        navHeader.setOnClickListener(this);

    }



    private void populateNavHeader(View view){
        TextView profileName=view.findViewById(R.id.profile_name);
        TextView profileEmail=view.findViewById(R.id.profile_email);
        ImageView profileImage=view.findViewById(R.id.profile_photo_header);

        //load image from firebase
        if(!imageId.equals("")){
        StorageReference storageReference=FirebaseStorage.getInstance().getReference();
        StorageReference ref=storageReference.child("images/"+imageId);
        GlideApp.with(this)
                .load(ref)
                .into(profileImage);}
        //load image from firebase
        profileEmail.setText(email);
        profileName.setText(name);

    }
    @Override
    public void onClick(View v) {
        drawerLayout.closeDrawer(GravityCompat.START);
        Bundle data=new Bundle();
        data.putString("clientEmail",email);
        data.putString("clientName",name);
        data.putString("clientPhone",phone);
        data.putString("clientImageId",imageId);
        data.putDouble("Lat",lat);
        data.putDouble("Lng",lng);
        data.putStringArrayList("bookings",(ArrayList<String>) bookingIDs);
        ClientHomeFragmentDirections.ActionClientHomeFragmentToClientProfileFragment action=
                ClientHomeFragmentDirections.actionClientHomeFragmentToClientProfileFragment().setClientData(data);
        navController.navigate(action);
    }

}

