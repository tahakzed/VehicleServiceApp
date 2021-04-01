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
    private MutableLiveData<Client> clientMutableLiveData;
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
                            long chargesCar=(Long)data.getData().get("Charges Car");
                            long chargesBike=(Long)data.get("Charges Bike");
                            String imageId="";//data.get("ImageId").toString();
                            List<String> Bookings=(List<String>) data.get("Bookings");
                            admins.add(new Admin(name,email,phone,lat,lng,serviceStation,reviews,chargesCar,chargesBike,Bookings,imageId));
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
   public LiveData<Client> getClientDataWithEmail(String email){
        if(clientMutableLiveData.getValue()==null){
        db.document("Client/"+email).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.e("Error",error.getMessage());
                    return;
                }
                if(value!=null){

                    String name=value.get("Name").toString();
                    String email=value.get("Email").toString();
                    String phone=value.get("Phone").toString();
                    double lat=Double.parseDouble(value.get("Lat").toString());
                    double lng=Double.parseDouble(value.get("Lng").toString());
                    List<String> vehicles=(List<String>) value.get("Vehicles");
                    List<String> Bookings=(List<String>)value.get("Bookings");
                    String imageId=value.get("ImageId").toString();
                    Log.d("MY VIEW MODEL", "onEvent: "+imageId);
                    Client client=new Client(name,email,phone,lat,lng,vehicles,Bookings,imageId);
                    clientMutableLiveData.postValue(client);
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
        if(bookingIds.size()>0){

           db.collection("Bookings").whereIn("Booking-ID",bookingIds).addSnapshotListener(new EventListener<QuerySnapshot>() {
               @Override
               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                   if(error!=null){
                       Log.d("BOOKINGS ERROR: ",error.getMessage());
                   }
                   if(value!=null){
                       List<DocumentSnapshot> docs=value.getDocuments();
                       bookings=new ArrayList<>();
                       Log.d("MyViewModel", "onEvent: doc.size()"+docs.size());
                       for(DocumentSnapshot doc : docs){
                           Log.d("ViewModel", "onEvent: Vehicle Name: "+ doc.get("Vehicle Name"));
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
                                   (Long)doc.get("Payment Tip"),
                                   (Boolean) doc.get("isSeenByClient"),
                                   doc.get("Client Image Id").toString(),
                                   doc.get("Admin Image Id").toString()));
                       }
                       clientBookings.postValue(bookings);
                   }
                   else{
                       Log.d("BOOKINGS ERROR:","value is null");
                   }
               }
           });
        }
        return clientBookings;
    }
}
