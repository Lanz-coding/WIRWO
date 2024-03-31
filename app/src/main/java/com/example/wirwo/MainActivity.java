package com.example.wirwo;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import androidx.core.app.NotificationCompat;

public class MainActivity extends Activity {

    // Splash screen display duration in milliseconds
    private static final int SPLASH_DISPLAY_DURATION = 3500; // 3.5 seconds
    private static final long DELAY_BEFORE_VIDEO_PLAYBACK = 1000; // 1 second delay

    private static final String CHANNEL_ID = "my_channel_id"; // Notification Channel ID
    private static final int NOTIFICATION_ID = 1;

    private VideoView videoView; // VideoView member variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the VideoView
        videoView = findViewById(R.id.videoView);

        // Set up a Handler to delay the start of video playback
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set the video path by getting the URI from the drawable resource
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wirwo_splash_vid);
                videoView.setVideoURI(videoUri);

                // Start playing the video
                videoView.start();
            }
        }, DELAY_BEFORE_VIDEO_PLAYBACK);

        // Create notification channel
        createNotificationChannel();

        // Display the splash screen for a specified duration
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Send notification after the splash screen duration
                sendNotification("Welcome", "Welcome to your app!");
                // Create an Intent to start LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close MainActivity to prevent going back to it when pressing back button from LoginActivity
            }
        }, SPLASH_DISPLAY_DURATION);
    }

    // Create a notification channel for devices with Android Oreo and above
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method to send a notification
    private void sendNotification(String title, String message) {
        // Create an explicit intent for an activity in your app
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_wirwo_transparent) // Set your notification icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Dismiss the notification when clicked

        // Trigger the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
