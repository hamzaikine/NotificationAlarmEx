package io.github.hamzaikine.mynotification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "13";
    Button showNotification, stopNotification;

    // Allows us to notify the user that something happened in the background
    NotificationManager notificationManager;

    // Used to track notifications
    int notificationId = 13;

    // Used to track if notification is active in the task bar
    boolean isNotificActive = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showNotification = findViewById(R.id.showNotification);
        stopNotification = findViewById(R.id.stopNotification);
        createNotificationChannel();
    }

    public void showNotification(View view) {

        // Define that we have the intention of opening MoreInfoNotification
        Intent moreInfoIntent = new Intent(this, NotificationDetails.class);

        // Used to stack tasks across activites so we go to the proper place when back is clicked
         moreInfoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, moreInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Builds a notification
        NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("My notification")
                .setContentText("New Message: Hello world!")
                .setTicker("Alert New Message")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.icon);


        //Gets a NotificationManager which is used to notify the user of the background event
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, notificBuilder.build());

        // Used so that we can't stop a notification that has already been stopped
        isNotificActive = true;

    }

    public void stopNotification(View view) {
        if(isNotificActive == true){
            notificationManager.cancel(notificationId);
        }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void setAlarm(View view) {

        // Define a time value of 5 seconds
        Long alertTime = SystemClock.elapsedRealtime()+10*1000;

        Log.d("alertTime", alertTime.toString());

        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alertIntent.putExtra(AlertReceiver.CHANNEL_ID,CHANNEL_ID);


        // Allows you to schedule for your application to do something at a later date
        // even if it is in he background or isn't active
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // set() schedules an alarm to trigger
        // Trigger for alertIntent to fire in 5 seconds
        // FLAG_UPDATE_CURRENT : Update the Intent if active
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alertTime,
                PendingIntent.getBroadcast(this, 1, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));

        Log.d("receivedAlert","Alert received");


    }

}
