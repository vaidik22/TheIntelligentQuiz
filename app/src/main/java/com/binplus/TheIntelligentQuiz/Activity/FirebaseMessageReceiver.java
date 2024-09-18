package com.binplus.TheIntelligentQuiz.Activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.binplus.TheIntelligentQuiz.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
NotificationManager notificationManager;
    // Override onNewToken to get new token
    @Override
    public void onNewToken(@NonNull String token)
    {
        Log.d("TAG", "Refreshed token: " + token);
    }
    // Override onMessageReceived() method to extract the
    // title and
    // body from the message passed in FCM
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("dxcfbhj", "fghjff");
        Log.e("dcfvghj", remoteMessage.getData().toString());
        Log.e("dcfvghj", remoteMessage.getNotification().getBody());
        Log.e("dcfvghj", remoteMessage.getNotification().getTitle());

        // Initialize the NotificationManager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "notification_channel", "web_app", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        if (remoteMessage.getNotification() != null) {
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
    }

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title, String message) {

        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon, R.drawable.savings);

        // Set onClick PendingIntent for tv_page1
        Intent intentPage1 = new Intent(this, HomeActivity.class);
        intentPage1.putExtra("page", "page1"); // Pass page identifier as extra
        PendingIntent pendingIntentPage1 = PendingIntent.getActivity(
                this, 0, intentPage1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.tv_page1, pendingIntentPage1);

        // Set onClick PendingIntent for tv_page2
        Intent intentPage2 = new Intent(this, HomeActivity.class);
        intentPage2.putExtra("page", "page2"); // Pass page identifier as extra
        PendingIntent pendingIntentPage2 = PendingIntent.getActivity(
                this, 1, intentPage2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.tv_page2, pendingIntentPage2);

        return remoteViews;
    }


    // Method to display the notifications
    public void showNotification(String title,
                                 String message)
    {
        // Pass the intent to switch to the MainActivity
        Intent intent = new Intent(this, HomeActivity.class);
        // Assign channel ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notification_channel", "FCM Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        String channel_id = "notification_channel";
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent
            = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
            = new NotificationCompat
                  .Builder(getApplicationContext(),
                           channel_id)
                  .setSmallIcon(R.drawable.savings)
                  .setAutoCancel(true)
                  .setVibrate(new long[] { 1000, 1000, 1000,
                                           1000, 1000 })
                  .setOnlyAlertOnce(true)
                  .setContentIntent(pendingIntent);

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(
                getCustomDesign(title, message));
        } // If Android Version is lower than Jelly Beans,
        // customized layout cannot be used and thus the
        // layout is set as follows
        else {
            builder = builder.setContentTitle(title)
                          .setContentText(message)
                          .setSmallIcon(R.drawable.savings);
        }
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(1233, builder.build());
    }
}
