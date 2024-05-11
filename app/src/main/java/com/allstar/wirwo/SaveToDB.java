package com.allstar.wirwo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SaveToDB {

    private static final String TAG = "SaveToDB"; // Changed tag name

    DatabaseHelper dbHelper = new DatabaseHelper();

    public static void saveUserData(double soilTemp, double soilMoisture, double humidity, double airTemp) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a HashMap to store sensor data
        Map<String, Object> sensorData = new HashMap<>();


        // Get the current date and time
        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
        }

        // Define a format for the date and time
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("MM-dd-yy_HH:mm:ss");
        }

        // Format the date and time using the defined format
        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDateTime = currentDateTime.format(formatter);
        }

        // Populate sensor data
        sensorData.put("timestamp", formattedDateTime); // Current timestamp
        sensorData.put("soilTemp", soilTemp);
        sensorData.put("soilMoisture", soilMoisture);
        sensorData.put("humidity", humidity);
        sensorData.put("airTemp", airTemp);

        // Add a new document with the current time as the document title
        db.collection("sensorHistory")
                .document(String.valueOf(formattedDateTime))
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
