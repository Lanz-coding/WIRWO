package com.allstar.wirwo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class SaveToDB {

    private static final String TAG = "SaveToDB"; // Changed tag name

    public static void saveUserData() {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the current time in milliseconds
        long currentTime = System.currentTimeMillis();

        // Create a HashMap to store sensor data
        Map<String, Object> sensorData = new HashMap<>();

        // Populate sensor data
        sensorData.put("timestamp", System.currentTimeMillis()); // Current timestamp
        sensorData.put("soilTemp", 25.5); // Example soil temperature
        sensorData.put("soilMoisture", 60); // Example soil moisture
        sensorData.put("humidity", 55); // Example humidity
        sensorData.put("airTemp", 27.8); // Example air temperature


        // Add a new document with the current time as the document title
        db.collection("sensorHistory")
                .document(String.valueOf(currentTime))
                .set(sensorData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Sensor data saved successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding sensor data", e);
                    }
                });
    }
}
