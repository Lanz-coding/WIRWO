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

    private static double humidityValue;
    private boolean ventiValue, waterValue;
    private static double moistureValue;
    private static double tempValue;
    private static double airtempValue;
    private boolean alertsValue;
    private boolean notifsValue;

    private static double minSoilTempThreshold, maxSoilTempThreshold;
    private static double minAirTempThreshold, maxAirTempThreshold;
    private static double minSoilMoistureThreshold, maxSoilMoistureThreshold;
    private static double minHumidityThreshold, maxHumidityThreshold;

    public interface UsernameCallback {
        void onUsernameReceived(String username);
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    DatabaseHelper() {
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot sensorDataSnapshot = dataSnapshot.child("SensorData");
                humidityValue = getValueOrDefault(sensorDataSnapshot.child("Humidity"), 0.0);
                ventiValue = getValueOrDefault(sensorDataSnapshot.child("Ventilation"), false);
                waterValue = getValueOrDefault(sensorDataSnapshot.child("WaterPump"), false);
                moistureValue = getValueOrDefault(sensorDataSnapshot.child("Soil_Moisture"), 0.0);
                tempValue = getValueOrDefault(sensorDataSnapshot.child("Temperature"), 0.0);
                airtempValue = getValueOrDefault(sensorDataSnapshot.child("Temperature_DS18B20"), 0.0);

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();

                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    Log.d("Username", "User ID: " + userId);

                    DataSnapshot usernameSnapshot = dataSnapshot.child("users").child(userId);
                    username = getValueOrDefault(usernameSnapshot.child("username"), "User");

                    Log.d("Username", "Username: " + username);
                } else {
                    username = "User";
                }

                DataSnapshot notifSettingsSnapshot = dataSnapshot.child("notifSettings");
                alertsValue = getValueOrDefault(notifSettingsSnapshot.child("allowAlerts"), false);
                notifsValue = getValueOrDefault(notifSettingsSnapshot.child("allowNotifs"), false);

                DataSnapshot thresholdSnapshot = dataSnapshot.child("thresholds");
                minSoilTempThreshold = getValueOrDefault(thresholdSnapshot.child("soilTempThreshold").child("min"), 20.0);
                maxSoilTempThreshold = getValueOrDefault(thresholdSnapshot.child("soilTempThreshold").child("max"), 30.0);
                minAirTempThreshold = getValueOrDefault(thresholdSnapshot.child("airTempThreshold").child("min"), 20.0);
                maxAirTempThreshold = getValueOrDefault(thresholdSnapshot.child("airTempThreshold").child("max"), 32.0);
                minSoilMoistureThreshold = getValueOrDefault(thresholdSnapshot.child("soilMoistureThreshold").child("min"), 50.0);
                maxSoilMoistureThreshold = getValueOrDefault(thresholdSnapshot.child("soilMoistureThreshold").child("max"), 90.0);
                minHumidityThreshold = getValueOrDefault(thresholdSnapshot.child("humidityThreshold").child("min"), 60.0);
                maxHumidityThreshold = getValueOrDefault(thresholdSnapshot.child("humidityThreshold").child("max"), 90.0);

                for (OnDataChangeListener listener : listeners) {
                    listener.onDatabaseChange(humidityValue, ventiValue, waterValue, moistureValue, tempValue, airtempValue, alertsValue, notifsValue,
                            minSoilTempThreshold, maxSoilTempThreshold,
                            minSoilMoistureThreshold, maxSoilMoistureThreshold,
                            minHumidityThreshold, maxHumidityThreshold,
                            minAirTempThreshold, maxAirTempThreshold);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private <T> T getValueOrDefault(DataSnapshot dataSnapshot, T defaultValue) {
        if (dataSnapshot.exists()) {
            return dataSnapshot.getValue((Class<T>) defaultValue.getClass());
        } else {
            return defaultValue;
        }
    }

    @Override
    public void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue,
                                 double minSoilTempThresh, double maxSoilTempThresh,
                                 double minSoilMoistureThresh, double maxSoilMoistureThresh,
                                 double minHumidityThresh, double maxHumidityThresh,
                                 double minAirTempThresh, double maxAirTempThresh) {
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

    public static double getMinSoilTempThreshold() {
        return minSoilTempThreshold;
    }

    public static double getMaxSoilTempThreshold() {
        return maxSoilTempThreshold;
    }

    public static double getMinAirTempThreshold() {
        return minAirTempThreshold;
    }

    public static double getMaxAirTempThreshold() {
        return maxAirTempThreshold;
    }

    public static void setSoilMoistureThreshold(double minValue, double maxValue){
        // Get a reference to the Firebase Database
        DatabaseReference myRef = databaseRef.child("thresholds").child("soilMoistureThreshold");

        // Set the values
        myRef.child("min").setValue(minValue);
        myRef.child("max").setValue(maxValue);
    }

    public static double getMinSoilMoistureThreshold() {
        return minSoilMoistureThreshold;
    }

    public static double getMaxSoilMoistureThreshold() {
        return maxSoilMoistureThreshold;
    }

    public static void setHumidityThreshold(double minValue, double maxValue){
        // Get a reference to the Firebase Database
        DatabaseReference myRef = databaseRef.child("thresholds").child("humidityThreshold");

        // Set the values
        myRef.child("min").setValue(minValue);
        myRef.child("max").setValue(maxValue);
    }

    public static double getMinHumidityThreshold() {
        return minHumidityThreshold;
    }

    public static double getMaxHumidityThreshold() {
        return maxHumidityThreshold;
    }

    public static double getSoilTempValue() {return tempValue;}
    public static double getSoilMoistureValue() {return moistureValue;}
    public static double getHumidityValue() {return humidityValue;}
    public static double getAirTempValue() {return airtempValue;}

    public void retrieveDashboardInitialData(DashboardActivity activity) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity != null) {
                    activity.updateUIElements(humidityValue, ventiValue, waterValue, moistureValue, tempValue, airtempValue, alertsValue, notifsValue,
                            minSoilTempThreshold, maxSoilTempThreshold,
                            minSoilMoistureThreshold, maxSoilMoistureThreshold,
                            minHumidityThreshold, maxHumidityThreshold,
                            minAirTempThreshold, maxAirTempThreshold);
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
                    activity.fetchDataAndUpdateUI(alertsValue, notifsValue, minSoilTempThreshold, maxSoilTempThreshold,
                            minAirTempThreshold, maxAirTempThreshold, minSoilMoistureThreshold, maxSoilMoistureThreshold,
                            minHumidityThreshold, maxHumidityThreshold);
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


    public void retrieveDataAnalysisInitialData(DataAnalyticsActivity activity) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity != null) {
                    activity.updateUIElements(humidityValue, ventiValue, waterValue, moistureValue, tempValue, airtempValue, alertsValue, notifsValue,
                            minSoilTempThreshold, maxSoilTempThreshold,
                            minSoilMoistureThreshold, maxSoilMoistureThreshold,
                            minHumidityThreshold, maxHumidityThreshold,
                            minAirTempThreshold, maxAirTempThreshold);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }


}
