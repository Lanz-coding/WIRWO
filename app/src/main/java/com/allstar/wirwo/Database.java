package com.allstar.wirwo;

import com.google.firebase.database.*;

public class Database {
    private DatabaseReference databaseRef;

    private double humidityValue;
    private boolean ledValue;
    private double moistureValue;
    private double tempValue;
    private double airtempValue;
    private boolean alertsValue;
    private boolean notifsValue;
    private static double soilTempThreshold;

    public Database() {
        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Set the reference path accordingly
        databaseRef = database.getReference();

        // Add listener to retrieve data and store it in variables
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Retrieve SensorData
                DataSnapshot sensorDataSnapshot = dataSnapshot.child("SensorData");
                humidityValue = sensorDataSnapshot.child("Humidity").getValue(Double.class);
                ledValue = sensorDataSnapshot.child("LED_Control").getValue(Boolean.class);
                moistureValue = sensorDataSnapshot.child("Soil_Moisture").getValue(Double.class);
                tempValue = sensorDataSnapshot.child("Temperature").getValue(Double.class);
                airtempValue = sensorDataSnapshot.child("Temperature_DS18B20").getValue(Double.class);

                // Retrieve notifSettings
                DataSnapshot notifSettingsSnapshot = dataSnapshot.child("notifSettings");
                alertsValue = notifSettingsSnapshot.child("allowAlerts").getValue(Boolean.class);
                notifsValue = notifSettingsSnapshot.child("allowNotifs").getValue(Boolean.class);

                // Retrieve thresholds
                soilTempThreshold = dataSnapshot.child("thresholds").child("soilTempThreshold").getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    // Getter methods for accessing the stored values
    public double getHumidityValue() {
        return humidityValue;
    }

    public boolean isLedValue() {
        return ledValue;
    }

    public double getMoistureValue() {
        return moistureValue;
    }

    public double getTempValue() {
        return tempValue;
    }

    public double getAirtempValue() {
        return airtempValue;
    }

    public boolean isAlertsValue() {
        return alertsValue;
    }

    public boolean isNotifsValue() {
        return notifsValue;
    }

    public static Integer getSoilTempThreshold() {
        return (int) soilTempThreshold;
    }
}