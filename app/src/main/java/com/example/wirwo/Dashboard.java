package com.example.wirwo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Dashboard extends Activity {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private SwitchMaterial ventiSwitch;
    private String LED_CONTROL_PATH = "LED_Control"; // Separate node for LED control
    private DatabaseReference ledControlRef;
    private PopupWindowHelper popupMenuHelper;

    private TextView soilTempText, airTempText, humidityText, moistureText;
    private ProgressBar soilTempBar, airTempBar, humidityBar, moistureBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Get references to TextViews and ProgressBars
        soilTempText = findViewById(R.id.soiltemp_meter);
        soilTempBar = findViewById(R.id.soiltemp_bar);
        airTempText = findViewById(R.id.airtemp_meter);
        airTempBar = findViewById(R.id.airtemp_bar);
        humidityText = findViewById(R.id.humidity_meter);
        humidityBar = findViewById(R.id.humidity_bar);
        moistureText = findViewById(R.id.moisture_meter);
        moistureBar = findViewById(R.id.moisture_bar);

        // Get an instance of Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference("SensorData");

        // Get current user
        FirebaseUser currentUser = auth.getCurrentUser();

        // Get reference to TextView
        TextView welcomeText = findViewById(R.id.welcome_text);

        // Check if user is not null
        if (currentUser != null) {
            // Extract the email address
            String email = currentUser.getEmail();

            // If email is not null, extract the username (prefix before "@")
            if (email != null) {
                int index = email.indexOf('@');
                if (index != -1) {
                    String username = email.substring(0, index);
                    welcomeText.setText("Ciao, " + username + "! Check your Wireless Worms Today!");
                } else {
                    // Handle case where email doesn't contain "@" symbol
                    welcomeText.setText("Ciao, User! Check your Wireless Worms Today!");
                }
            } else {
                // Handle case where currentUser.getEmail() is null
                welcomeText.setText("Ciao, User! Check your Wireless Worms Today!");
            }
        } else {
            // Set default text if user is null
            welcomeText.setText("Welcome, please sign in to proceed.");
        }

        // Initialize PopupMenuHelper with context of your activity
        popupMenuHelper = new PopupWindowHelper(this);

        SwitchMaterial waterPumpSwitch = findViewById(R.id.waterPumpSwitch);
        SwitchMaterial ventiSwitch = findViewById(R.id.ventiSwitch);

        waterPumpSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Water pump switch is turned on
                Toast.makeText(Dashboard.this, "Water Pump is turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Water pump switch is turned off
                Toast.makeText(Dashboard.this, "Water Pump is turned off", Toast.LENGTH_SHORT).show();
            }
        });

        ledControlRef = mDatabase.child(LED_CONTROL_PATH); // New DatabaseReference for LED control

        ventiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Ventilation switch is turned on, send "true" to Firebase
                turnOnLED();
                Toast.makeText(Dashboard.this, "Ventilation is turned on and LED is lit", Toast.LENGTH_SHORT).show();
            } else {
                // Ventilation switch is turned off, send "false" to Firebase
                turnOffLED();
                Toast.makeText(Dashboard.this.getApplicationContext(), "Ventilation is turned off and LED is off", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.toolbar_navigation_icon).setOnClickListener(v -> {
            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v);
        });


        // Add ValueEventListener to listen for changes in Firebase Database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    // Extract sensor data from snapshot
                    Double soilTemp = snapshot.child("Temperature_DS18B20").getValue(Double.class);
                    Double airTemp = snapshot.child("Temperature").getValue(Double.class);
                    Double humidity = snapshot.child("Humidity").getValue(Double.class);
                    Double moisture = snapshot.child("Soil_Moisture").getValue(Double.class);

                    // Update TextViews and ProgressBars with sensor data
                    if (soilTemp != null) {
                        soilTempText.setText(String.format("%.2f", soilTemp) + "°C");
                        soilTempBar.setProgress((int) Math.round(soilTemp)); // Assuming progress bar max is 100
                    }
                    if (airTemp != null) {
                        airTempText.setText(String.format("%.2f", airTemp) + "°C");
                        airTempBar.setProgress((int) Math.round(airTemp)); // Assuming progress bar max is 100
                    }
                    if (humidity != null) {
                        humidityText.setText(String.format("%.2f", humidity) + "%");
                        humidityBar.setProgress(humidity.intValue()); // Assuming progress bar max is 100
                    }
                    if (moisture != null) {
                        moistureText.setText(String.format("%.2f", moisture) + "%");
                        moistureBar.setProgress(moisture.intValue()); // Assuming progress bar max is 100
                    }

                    boolean ventiChecked = snapshot.child("LED_Control").getValue(boolean.class);

                    if (ventiChecked == true) {
                        ventiSwitch.setChecked(true);
                    } else {
                        ventiSwitch.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
                Toast.makeText(Dashboard.this, "Error fetching sensor data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void turnOnLED() {
        ledControlRef.setValue(true);
    }

    private void turnOffLED() {
        ledControlRef.setValue(false);
    }


}
