package com.example.vehicleserviceapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminBackgroundProcess extends BroadcastReceiver {
    private Context context;
    private List<String> reviewList;
    private FirebaseFirestore db;
    private String adminEmail;
    public AdminBackgroundProcess(){
        super();
        db=FirebaseFirestore.getInstance();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
    this.context=context;
    reviewList=(List<String>)intent.getSerializableExtra("reviewList");
    adminEmail=intent.getStringExtra("adminEmail");

    reviewsNotification();

    }
    private void reviewsNotification(){
        db.document("Admin/"+adminEmail).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    List<String> adminReviews = (List<String>) value.get("Reviews");
                    if (reviewList != null) {
                        if (reviewList.size() != adminReviews.size() || !reviewList.containsAll(adminReviews)) {
                            notifyOnBookingChanged("New Review!", "0200123", "You got a new review!", null);
                        }
                    }
                        List<String> bIds = (List<String>) value.get("Bookings");
                        if(bIds==null)
                            return;
                        if(bIds.size()==0)
                            return;
                        db.collection("Bookings").whereIn("Booking-ID", bIds).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null) {
                                    List<DocumentChange> documentChanges = value.getDocumentChanges();
                                    for (DocumentChange docChange : documentChanges) {
                                        QueryDocumentSnapshot doc = docChange.getDocument();
                                        String clientName = doc.get("Client Name").toString();
                                        String bookingId = doc.getId();
                                        String status = doc.get("Status").toString();
                                        boolean isSeenByAdmin = (Boolean) doc.get("isSeenByAdmin");
                                        if (status.equals("Canceled")) {
                                            db.collection("Bookings")
                                                    .document(bookingId).delete();
                                            bIds.remove(bookingId);
                                            Map<String, Object> aMap = new HashMap<>();
                                            aMap.put("Bookings", bIds);
                                            db.collection("Admin").document(adminEmail)
                                                    .update(aMap);
                                            notifyOnBookingChanged("Booking Canceled!", bookingId, "Your client " + clientName +
                                                    " canceled their booking with you", null);
                                        } else if (status.equals("Pending") && !isSeenByAdmin) {
                                            notifyOnBookingChanged("New Booking!", bookingId, "You got a new booking from " + clientName,
                                                    null);
                                            Map<String, Object> bMap = new HashMap<>();
                                            bMap.put("isSeenByAdmin", true);
                                            db.collection("Bookings")
                                                    .document(bookingId).update(bMap);
                                        }

                                    }
                                }
                            }
                        });

                }
            }
        });
        SystemClock.sleep(10);
    }

    private void createNotificationChannel(String bookingId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "adminBookings"+bookingId;
            String description = "Notification channel for adminBooking"+bookingId;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("adminBookings"+bookingId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void notifyOnBookingChanged(String title,String bookingId, String message, PendingIntent pendingIntent){
        createNotificationChannel(bookingId);
        Intent intent = new Intent(context, ClientBookingFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(pendingIntent==null)
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"adminBookings"+bookingId)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.car_wash_logo))
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

    }
}
