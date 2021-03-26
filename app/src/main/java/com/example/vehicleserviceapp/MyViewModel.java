package com.example.vehicleserviceapp;

import android.app.Application;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyViewModel extends AndroidViewModel {
    private FirebaseFirestore db;
    private MutableLiveData<List<Admin>> adminList;
    private MutableLiveData<List<Client>> clientMutableLiveData;
    private MutableLiveData<List<Booking>> clientBookings;
    private List<Booking> bookings;


    public MyViewModel(Application application){
        super(application);
        db=FirebaseFirestore.getInstance();
        adminList=new MutableLiveData<>();
        clientMutableLiveData=new MutableLiveData<>();
        clientBookings=new MutableLiveData<>();

    }
    public LiveData<List<Admin>> getAllAdmins(){
        if(adminList.getValue()==null){
            db.collection("Admin").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error!=null){
                        Log.e("FIREBASE ERROR: ",error.getMessage());
                    }
                    if(value!=null){   //listen to real time updates
                        List<DocumentSnapshot> documentSnapshotList=value.getDocuments();
                        List<Admin> admins=new ArrayList<>();
                        for(DocumentSnapshot data : documentSnapshotList){
                            Log.i("FIREBASE DATA",data.getData().toString());
                            String name=data.get("Name").toString();
                            String email=data.get("Email").toString();
                            String phone = data.get("Phone").toString();
                            String serviceStation=data.get("Service Station").toString();
                            double lat=Double.parseDouble(data.get("Lat").toString());
                            double lng=Double.parseDouble(data.get("Lng").toString());
                            List<String> reviews=(List<String>) data.get("Reviews");
                            long charges=(Long)data.getData().get("Charges");
                            List<String> Bookings=(List<String>) data.get("Bookings");
                            admins.add(new Admin(name,email,phone,lat,lng,serviceStation,reviews,(int)charges,Bookings));
                        }
                        adminList.postValue(admins);
                    }
                    else{
                        Log.e("FIREBASE ERROR:","value is null");
                    }
                }
            });
        }

        return adminList;
    }
   public LiveData<List<Client>> getClientDataWithEmail(String email){
        if(clientMutableLiveData.getValue()==null){
        db.document("Client/"+email).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.e("Error",error.getMessage());
                    return;
                }
                if(value!=null){
                    Log.d(TAG,"CLIENT DATA:"+value.getData());
                    List<Client> clientList=new ArrayList<>();
                    String name=value.get("Name").toString();
                    String email=value.get("Email").toString();
                    String phone=value.get("Phone").toString();
                    double lat=Double.parseDouble(value.get("Lat").toString());
                    double lng=Double.parseDouble(value.get("Lng").toString());
                    List<String> vehicles=(List<String>) value.get("Vehicles");
                    List<String> Bookings=(List<String>)value.get("Bookings");
                    clientList.add(new Client(name,email,phone,lat,lng,vehicles,Bookings));
                    clientMutableLiveData.postValue(clientList);
                }
                else{
                    Log.e(TAG, "onEvent: Value is NUll");
                }

            }
        });
        }
        return clientMutableLiveData;
    }
    public LiveData<List<Booking>> getBookingsDataWithIds(List<String> bookingIds){
        if(clientBookings.getValue()==null && bookingIds.size()>0){

           db.collection("Bookings").whereIn("Booking-ID",bookingIds).addSnapshotListener(new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                   if(error!=null){
                       Log.e("BOOKINGS ERROR: ",error.getMessage());
                   }
                   if(value!=null){
                       List<DocumentSnapshot> docs=value.getDocuments();
                       bookings=new ArrayList<>();
                       for(DocumentSnapshot doc : docs){
                           if(!doc.exists())
                               continue;
                           bookings.add(new Booking(doc.getId(),
                                   doc.get("Status").toString(),
                                   doc.get("Service Station").toString(),
                                   doc.get("Client Name").toString(),
                                   doc.get("Client Phone").toString(),
                                   doc.get("Client Email").toString(),
                                   (Double)doc.get("Lat"),
                                   (Double)doc.get("Lng"),
                                   doc.get("Vehicle Name").toString(),
                                   doc.get("Vehicle Type").toString(),
                                   doc.get("Date").toString(),
                                   doc.get("Time").toString(),
                                   doc.get("Admin Email").toString(),
                                   doc.get("Payment Status").toString(),
                                   doc.get("Payment Date").toString(),
                                   doc.get("Payment Time").toString(),
                                   (Long)doc.get("Payment Charges"),
                                   (Long)doc.get("Payment Tip")));
                       }
                       clientBookings.postValue(bookings);
                   }
                   else{
                       Log.e("BOOKINGS ERROR:","value is null");
                   }
               }
           });
        }
        return clientBookings;
    }
}
