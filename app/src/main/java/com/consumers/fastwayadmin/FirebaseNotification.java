package com.consumers.fastwayadmin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.consumers.fastwayadmin.Login.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class FirebaseNotification extends FirebaseMessagingService {

    DatabaseReference ref;
    FirebaseAuth auth;
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {

            // Since the notification is received directly from
            // FCM, the title and the body can be fetched
            // directly as below.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                String title = remoteMessage.getNotification().getTitle();
                String message = remoteMessage.getNotification().getBody();
                if(message.contains("Customer has requested to pay amount in Cash")){
                    Intent intents = new Intent("myFunction");
                    // add data
                    intents.putExtra("value1", title);
                    intents.putExtra("value2", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intents);
                }


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel = new NotificationChannel("notification_channel", "web_app", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "notification_channel");
                notificationBuilder.setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ordinalo)
                        .setTicker(remoteMessage.getNotification().getTitle())
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody());
                notificationManager.notify(1, notificationBuilder.build());
            }else {
                showNotification(
                        remoteMessage.getNotification().getTitle(),
                        remoteMessage.getNotification().getBody() , "");
            }
        }
    }
    private RemoteViews getCustomDesign(String title,
                                        String message) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.drawable.foodinelogo);
        return remoteViews;
    }
    // Method to display the notifications
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showNotification(String title,
                                 String message, String action) {
        auth = FirebaseAuth.getInstance();
        notificationClass notificationClass = new notificationClass(title,message, ServerValue.TIMESTAMP);
        ref = FirebaseDatabase.getInstance().getReference().getRoot().child("Admin").child(Objects.requireNonNull(auth.getUid()));
        ref.child("Notification").child(System.currentTimeMillis() + "").setValue(notificationClass);
        // Pass the intent to switch to the MainActivity
        Intent intent
                = new Intent(this, MainActivity.class);
        // Assign channel ID
        String channel_id = "notification_channel";
//        boolean reply = false;
//        String[] arr = action.split(",");
//        RemoteInput remoteInput;
//        if(arr[0].equals("Chat")){
//            final String KEY_TEXT_REPLY = "key_text_reply";
//
////            String replyLabel = getResources().getString(R.string.reply_label);
//             remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
//                    .setLabel("Reply")
//                    .build();
//
//            NotificationCompat.Action actions =
//                    new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,)
//                            .addRemoteInput(remoteInput)
//                            .build();
//
//             return;
//        }

        if(message.contains("Customer has requested to pay amount in Cash")){
            Intent intents = new Intent("myFunction");
            // add data
            intents.putExtra("value1", title);
            intents.putExtra("value2", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intents);
        }
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.S) {
            pendingIntent
                    = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_MUTABLE);
        }else{
            pendingIntent
                    = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE);
        }


        SharedPreferences preferences = this.getSharedPreferences("value",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("title",title);
        editor.putString("message",message);
        editor.apply();

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setSmallIcon(R.drawable.foodinelogo)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(
                getCustomDesign(title, message));
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }


        notificationManager.notify(0, builder.build());
    }
}
