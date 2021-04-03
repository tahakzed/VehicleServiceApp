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
import androidx.navigation.ui.NavigationUI;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMainActivity extends AppCompatActivity implements View.OnClickListener {
    MaterialToolbar topAppBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View navHeader;
    String email,name,phone,serviceStationName,imageId;
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
        Intent intent=getIntent();
        email=intent.getStringExtra("Email");
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
                imageId=admin.getImageId();
                populateNavHeader(navHeader);
                bookingIDs=admin.getBookings();
                startAdminBackgroundProcess();
            }
        });
    }
    private void startAdminBackgroundProcess(){
        Intent intent=new Intent(getApplicationContext(),AdminBackgroundProcess.class);
        intent.putExtra("reviewList",(Serializable) reviewsList);
        intent.putExtra("bookingIds",(Serializable) bookingIDs);
        intent.putExtra("adminEmail",email);
        intent.setAction("adminBackgroundProcess");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,0,10,pendingIntent);
    }



    private void populateNavHeader(View view){
        TextView profileName=view.findViewById(R.id.profile_name);
        TextView profileEmail=view.findViewById(R.id.profile_email);
        ImageView profileImage=view.findViewById(R.id.profile_photo_header);
        //load image from firebase
        if(!imageId.equals(""))
        {StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        StorageReference ref=storageReference.child("images/"+imageId);
        GlideApp.with(this)
                .load(ref)
                .into(profileImage);}
        //load image from firebase
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
        bundle.putString("imageId",imageId);
        bundle.putStringArrayList("bookings",(ArrayList<String>) bookingIDs);
        bundle.putStringArrayList("reviews",(ArrayList<String>) reviewsList);
        bundle.putString("serviceStationName",serviceStationName);
        AdminHomeFragmentDirections.ActionAdminHomeFragmentToAdminProfileFragment action=
                AdminHomeFragmentDirections.actionAdminHomeFragmentToAdminProfileFragment(bundle).setAdminProfileData(bundle);
        navController.navigate(action);
    }
}