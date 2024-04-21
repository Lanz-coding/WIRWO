package com.allstar.wirwo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper implements OnDataChangeListener {

    private static DatabaseHelper instance;
    private FirebaseDatabase database;

    private static FirebaseAuth auth;
    private static FirebaseUser currentUser;
    private static DatabaseReference databaseRef;
    private List<OnDataChangeListener> listeners = new ArrayList<>();

    private String username;

    private double humidityValue;
    private boolean ventiValue, waterValue;
    private double moistureValue;
    private double tempValue;
    private double airtempValue;
    private boolean alertsValue;
    private boolean notifsValue;

    private static double soilTempThreshold, airTempThreshold, soilMoistureThreshold, humidityThreshold;

    public interface UsernameCallback {
        void onUsernameReceived(String username);
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    private DatabaseHelper() {
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot sensorDataSnapshot = dataSnapshot.child("SensorData");
                humidityValue = sensorDataSnapshot.child("Humidity").getValue(Double.class);
                ventiValue = sensorDataSnapshot.child("Ventilation").getValue(Boolean.class);
                waterValue = sensorDataSnapshot.child("WaterPump").getValue(Boolean.class);
                moistureValue = sensorDataSnapshot.child("Soil_Moisture").getValue(Double.class);
                tempValue = sensorDataSnapshot.child("Temperature").getValue(Double.class);
                airtempValue = sensorDataSnapshot.child("Temperature_DS18B20").getValue(Double.class);

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();

                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    Log.d("Username", "User ID: " + userId);

                    DataSnapshot usernameSnapshot = dataSnapshot.child("users").child(userId);
                    username = usernameSnapshot.child("username").getValue(String.class);

                    Log.d("Username", "Username: " + username);
                } else {
                    username = "User";
                }


                DataSnapshot notifSettingsSnapshot = dataSnapshot.child("notifSettings");
                alertsValue = notifSettingsSnapshot.child("allowAlerts").getValue(Boolean.class);
                notifsValue = notifSettingsSnapshot.child("allowNotifs").getValue(Boolean.class);

                soilTempThreshold = dataSnapshot.child("thresholds").child("soilTempThreshold").getValue(Integer.class);
                soilMoistureThreshold = dataSnapshot.child("thresholds").child("soilMoistureThreshold").getValue(Integer.class);
                humidityThreshold = dataSnapshot.child("thresholds").child("humidityThreshold").getValue(Integer.class);
                airTempThreshold = dataSnapshot.child("thresholds").child("airTempThreshold").getValue(Integer.class);

                for (OnDataChangeListener listener : listeners) {
                    listener.onDatabaseChange(humidityValue, ventiValue, waterValue, moistureValue, tempValue, airtempValue, alertsValue, notifsValue, soilTempThreshold, soilMoistureThreshold, humidityThreshold, airTempThreshold);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    @Override
    public void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue,
                                 double soilTempThresh, double soilMoistureThresh, double humidityThresh, double airTempThresh) {
        // This method is not used in this approach directly, but can be used for internal purposes in DatabaseHelper if needed
    }

    public void addOnDataChangeListener(OnDataChangeListener listener) {
        listeners.add(listener);
    }

    public void removeOnDataChangeListener(OnDataChangeListener listener) {
        listeners.remove(listener);
    }

    public void setWaterValue(boolean value){
        // Get a reference to the Firebase Database
        DatabaseReference myRef = databaseRef.child("SensorData").child("WaterPump");

        // Set the values
        myRef.setValue(value); // waterValue is a boolean
    }

    public void setVentiValue(boolean value){
        // Get a reference to the Firebase Database
        DatabaseReference myRef = databaseRef.child("SensorData").child("Ventilation");

        // Set the values
        myRef.setValue(value); // ventiValue is a boolean
    }

    public static void setSoilTempThreshold(int value){
        // Get a reference to the Firebase Database
        DatabaseReference myRef = databaseRef.child("thresholds").child("soilTempThreshold");

        // Set the values
        myRef.setValue(value);
    }


    public static int getSoilTempThreshold() {
        return (int) soilTempThreshold;
    }

    public static int getSoilMoistureThreshold() {
        return (int) soilMoistureThreshold;
    }

    public static int getHumidityThreshold() {
        return (int) humidityThreshold;
    }

    public static int getAirTempThreshold() {
        return (int) airTempThreshold;
    }



    public void retrieveDashboardInitialData(DashboardActivity activity) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity != null) {
                    activity.updateUIElements(humidityValue, ventiValue, waterValue, moistureValue, tempValue, airtempValue, alertsValue, notifsValue);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    public void retrieveSettingsInitialData(SettingsActivity activity) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity != null) {
                    activity.fetchDataAndUpdateUI(alertsValue, notifsValue,soilTempThreshold, soilMoistureThreshold, humidityThreshold, airTempThreshold) ;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    public static void getUsername(FirebaseDatabase database, UsernameCallback callback) {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = database.getReference("users").child(userId);
            userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String retrievedUsername = dataSnapshot.getValue(String.class);
                    if (retrievedUsername != null) {
                        callback.onUsernameReceived(retrievedUsername);
                    } else {
                        callback.onUsernameReceived("User");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    callback.onUsernameReceived("User");
                }
            });
        } else {
            callback.onUsernameReceived("User");
        }
    }

    public static void getUserEmail(FirebaseAuth auth, EmailCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email != null) {
                callback.onEmailReceived(email);
            } else {
                callback.onEmailReceived("No email found");
            }
        } else {
            callback.onEmailReceived("No user signed in");
        }
    }
    public interface EmailCallback {
        void onEmailReceived(String email);
    }


}
