package com.example.stayathome.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.stayathome.R;

public class NotificationHelper {

    public static final String CHANNEL_ID_GROWTH_PROGRESS = "100";

    public void createGrowthProgressNotificationChannel(Context mainContext) {
        // Create the NotificationChannel, but only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mainContext.getResources().getString(R.string.growth_progress_channel_name);
            String description = mainContext.getResources().getString(R.string.growth_progress_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_GROWTH_PROGRESS, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notManager = mainContext.getSystemService(NotificationManager.class);
            if(notManager != null)
                notManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotification(Context context, String channelID, String title, String text){
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //builder.setContentIntent();
        builder.setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notManager = NotificationManagerCompat.from(context);
        notManager.notify(100, builder.build());
    }
} // End class NotificationHelper
