package com.allstar.wirwo;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import androidx.core.app.NotificationCompat;

public class SplashActivity extends Activity {

    private static final long DELAY_BEFORE_VIDEO_PLAYBACK = 1000; // 1 second delay

    private static final String CHANNEL_ID = "my_channel_id"; // Notification Channel ID
    private static final int NOTIFICATION_ID = 1;

    private VideoView videoView; // VideoView member variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize the VideoView
        videoView = findViewById(R.id.videoView);

        // Set up a Handler to delay the start of video playback
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set the video path by getting the URI from the drawable resource
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wirwo_splash_vid_new);
                videoView.setVideoURI(videoUri);

                // Start playing the video
                videoView.start();
            }
        }, DELAY_BEFORE_VIDEO_PLAYBACK);

        // Set a completion listener on the VideoView
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Video playback has completed, proceed to next screen
                proceedToNextScreen();
            }
        });

        // Create notification channel
        createNotificationChannel();
    }

    // Proceed to the next screen (LoginActivity)
    private void proceedToNextScreen() {
        // Send notification after the video playback has completed
        sendNotification("Welcome", "Welcome to your app!");
        // Create an Intent to start LoginActivity
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity to prevent going back to it when pressing back button from LoginActivity
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
