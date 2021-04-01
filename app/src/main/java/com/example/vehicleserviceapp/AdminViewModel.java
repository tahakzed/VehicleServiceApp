package com.example.vehicleserviceapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
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

public class AdminViewModel extends AndroidViewModel {
    private FirebaseFirestore db;
    private MutableLiveData<List<Admin>> adminMutableLiveData;
    private MutableLiveData<List<Booking>> adminBookings;
    private List<Booking> bookings;

    public AdminViewModel(@NonNull Application application) {
        super(application);
        db=FirebaseFirestore.getInstance();
        adminMutableLiveData=new MutableLiveData<>();
        adminBookings=new MutableLiveData<>();
    }
    public LiveData<List<Admin>> getAdminDataWithEmail(String email){
        if(adminMutableLiveData.getValue()==null){
            db.document("Admin/"+email).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                        double lat=(Double)value.get("Lat");
                        double lng=(Double)value.get("Lng");
                        String serviceStationName=value.get("Service Station").toString();
                        List<String> reviews=(List<String>)value.get("Reviews");
                        long chargesCar=(Long)value.get("Charges Car");
                        long chargesBike=(Long)value.get("Charges Bike");
                        List<String> bookings=(List<String>)value.get("Bookings");
                        String imageId=value.get("ImageId").toString();
                        List<Admin> admins=new ArrayList<>();
                        Admin admin=new Admin(name,email,phone,lat,lng,serviceStationName,reviews,chargesCar,chargesBike,bookings,imageId);
                        admins.add(admin);
                        adminMutableLiveData.postValue(admins);
                    }
                    else{
                        Log.e(TAG, "onEvent: Value is NUll");
                    }

                }
            });
        }
        return adminMutableLiveData;
    }
    public LiveData<List<Booking>> getBookingsDataWithIds(List<String> bookingIds){
        if(bookingIds.size()>0){

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
                                    (Long)doc.get("Payment Tip"),
                                    (Boolean) doc.get("isSeenByClient"),
                                    doc.get("Client Image Id").toString(),
                                    doc.get("Admin Image Id").toString()));
                        }
                        adminBookings.postValue(bookings);
                    }
                    else{
                        Log.e("BOOKINGS ERROR:","value is null");
                    }
                }
            });
        }
        return adminBookings;
    }
}
