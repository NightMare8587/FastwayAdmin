package com.consumers.fastwayadmin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.consumers.fastwayadmin.HomeScreen.HomeScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Restaurants").child(Objects.requireNonNull(auth.getUid())).child("Tables");
//
////       new Timer().scheduleAtFixedRate(new TimerTask() {
////           @Override
////           public void run() {
////               databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////                   @Override
////                   public void onDataChange(@NonNull DataSnapshot snapshot) {
////                       if(snapshot.exists()){
////                           for(DataSnapshot dataSnapshot : snapshot.getChildren()){
////                               String tableNum = dataSnapshot.child("tableNum").getValue(String.class);
////                               String time = String.valueOf(dataSnapshot.child("timeInMillis").getValue());
////                               int result = time.compareTo(String.valueOf(System.currentTimeMillis()));
////                               if(result < 0){
////                                   assert tableNum != null;
////                                   databaseReference.child(tableNum).child("customerId").removeValue();
////                                   databaseReference.child(tableNum).child("time").removeValue();
////                                   databaseReference.child(tableNum).child("timeInMillis").removeValue();
////                                   databaseReference.child(tableNum).child("status").setValue("available");
////                               }
////                           }
////                       }
////                   }
////
////                   @Override
////                   public void onCancelled(@NonNull DatabaseError error) {
////
////                   }
////               });
////           }
////       },0,5000);
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            startMyOwnForeground();
//        else
//            startForeground(1, new Notification());
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    private void startForeground() {
//        Intent notificationIntent = new Intent(this, HomeScreen.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//
//        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
//                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
//                .setOngoing(true)
//                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText("Service is running background")
//                .setContentIntent(pendingIntent)
//                .build());
//    }
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void startMyOwnForeground(){
//        String NOTIFICATION_CHANNEL_ID = "com.consumers.fastwayadmin";
//        String channelName = "My Background Service";
//        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
//        chan.setLightColor(Color.BLUE);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert manager != null;
//        manager.createNotificationChannel(chan);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
//                .setContentTitle("App is running in background")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//        startForeground(2, notification);
//    }
}