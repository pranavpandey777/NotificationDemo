package com.example.notificationdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btnNotifyMe, btnUpdate, btnCancel;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;


    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";


    private static final String ACTION_UPDATE_NOTIFICATION = "com.example.notificationdemo.ACTION_UPDATE_NOTIFICATION";

    private NotificationReceiver notificationReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
//defining receiver
        registerReceiver(notificationReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
//button
        btnNotifyMe = findViewById(R.id.btnNotifyMe);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
//state of button
        setNotificationButtonState(true, false, false);
        btnNotifyMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send notification
                sendNotification();
                setNotificationButtonState(false, true, true);
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update the notification
                updateNotification();
                setNotificationButtonState(false, false, true);

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //5. Cancel the notification
                cancelNotification();
                setNotificationButtonState(true, false, false);
            }
        });


    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the notification.
            updateNotification();

        }
    }

    public void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(this, "API level is greater than or equal to 26", Toast.LENGTH_SHORT).show();


            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "My Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from My Notification");
            /* notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);*/
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void sendNotification() {


        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = getNotificationBuilder();
//create button
        builder.addAction(R.drawable.abc, "Update" /*getString(R.string.update)*/, updatePendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    public NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.abc)
                .setContentTitle("You have been notified")
                .setContentText("This is your notification")
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return builder;
    }


    void setNotificationButtonState(Boolean isNotifyEnabled,
                                    Boolean isUpdateEnabled,
                                    Boolean isCancelEnabled) {
        btnNotifyMe.setEnabled(isNotifyEnabled);
        btnUpdate.setEnabled(isUpdateEnabled);
        btnCancel.setEnabled(isCancelEnabled);
    }

    private void updateNotification() {
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.abc);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated"));
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }

    private void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
