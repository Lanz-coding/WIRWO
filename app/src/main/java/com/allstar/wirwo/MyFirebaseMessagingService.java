//package com.allstar.wirwo;
//
//import android.util.Log;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    private static final String TAG = "MyFirebaseMsgService";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // Handle FCM messages here.
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            // Handle data payload here.
//        }
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            // Handle notification payload here.
//        }
//    }
//
//    @Override
//    public void onNewToken(String token) {
//        // Handle new token.
//        Log.d(TAG, "Refreshed token: " + token);
//        // You can send the token to your server or store it locally for later use.
//    }
//}
