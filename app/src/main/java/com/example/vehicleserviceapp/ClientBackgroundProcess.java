package com.example.vehicleserviceapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientBackgroundProcess extends BroadcastReceiver {

    private String clientName,clientEmail;
    private List<String> bookingIDs;
    private FirebaseFirestore db;
    private Context context;
    public ClientBackgroundProcess() {
        super();
        db=FirebaseFirestore.getInstance();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        clientName=intent.getStringExtra("clientName");
        bookingIDs=(List<String>) intent.getSerializableExtra("bookingIds");
        clientEmail=intent.getStringExtra("clientEmail");
        if(bookingIDs==null)
            return;
        if(bookingIDs.size()==0)
            return;
        db.collection("Bookings").whereIn("Booking-ID",bookingIDs).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    List<DocumentChange> documentChanges=value.getDocumentChanges();
                    for(DocumentChange docChange: documentChanges){
                        QueryDocumentSnapshot doc=docChange.getDocument();
                        String bookingId=doc.getId();
                        String status=doc.get("Status").toString();
                        boolean isSeenByClient=(Boolean)doc.get("isSeenByClient");
                        boolean isSeenInProgressByClient=(Boolean)doc.get("isSeenInProgressByClient");
                        String serviceStation=doc.get("Service Station").toString();
                        if (status.equals("Complete") && !isSeenByClient) {
                            Map<String, Object> bMap = new HashMap<>();
                            bMap.put("isSeenByClient", true);
                            db.collection("Bookings")
                                    .document(bookingId).update(bMap);
                            Intent intent = new Intent(context, ClientToAdminReviewActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addNextIntentWithParentStack(intent);
                            intent.putExtra("bookingId", bookingId);
                            intent.putExtra("clientName", clientName);
                            intent.setAction(Long.toString(System.currentTimeMillis()));
                            PendingIntent resultPendingIntent =
                                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            notifyOnBookingChanged(serviceStation,bookingId, "Your service was completed! Tap here to leave a review!", resultPendingIntent);
                        } else if (status.equals("Declined")) {

                            db.collection("Bookings").document(bookingId)
                                    .delete();
                            Map<String, Object> cMap = new HashMap<>();
                            bookingIDs.remove(bookingId);
                            cMap.put("Bookings", bookingIDs);
                            db.collection("Client").document(clientEmail)
                                    .update(cMap);
                            notifyOnBookingChanged(serviceStation,bookingId, "Your service request was declined", null);
                        }
                        else if (status.equals("In-Progress") && !isSeenInProgressByClient) {
                            Map<String, Object> bMap = new HashMap<>();
                            bMap.put("isSeenInProgressByClient", true);
                            db.collection("Bookings")
                                    .document(bookingId).update(bMap);
                            notifyOnBookingChanged(serviceStation,bookingId, "Your service request was accepted!", null);
                        }


                    }
                }
            }
        });
        SystemClock.sleep(10);
    }

    private void createNotificationChannel(String bookingId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "clientBookings"+bookingId;
            String description = "Notification channel for clientBooking"+bookingId;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("clientBookings"+bookingId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void notifyOnBookingChanged(String serviceStation,String bookingId, String message, PendingIntent pendingIntent){
        createNotificationChannel(bookingId);
        Intent intent = new Intent(context, ClientBookingFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(pendingIntent==null)
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"clientBookings"+bookingId)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.car_wash_logo))
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle(serviceStation)
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
