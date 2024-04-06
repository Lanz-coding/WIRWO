package com.example.wirwo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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

    private LoadingDialogHelper loadingDialogHelper;
    private Handler handler;

    private TextView soilTempText, airTempText, humidityText, moistureText;
    private ProgressBar soilTempBar, airTempBar, humidityBar, moistureBar;

    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Initialize the LoadingDialogHelper
        loadingDialogHelper = new LoadingDialogHelper(this);
        handler = new Handler();

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
            String userId = currentUser.getUid();

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("username");

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.getValue(String.class);
                        welcomeText.setText("Ciao, " + username + "! Check your Wireless Worms Today!");
                    } else {
                        // Handle case where username data doesn't exist
                        welcomeText.setText("Ciao, User! Check your Wireless Worms Today!"); // Or set a default message
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    welcomeText.setText("Ciao, User! Check your Wireless Worms Today!");
                }
            });
        } else {
            // Set default text if user is null
            String text = "User";
            welcomeText.setText("Ciao, User! Check your Wireless Worms Today!");
        }

        // Initialize PopupMenuHelper with context of your activity
        popupMenuHelper = new PopupWindowHelper(this);

        SwitchMaterial waterPumpSwitch = findViewById(R.id.waterPumpSwitch);
        ventiSwitch = findViewById(R.id.ventiSwitch);

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
                if (isFirstTime) {
                    // Show loading animation only for the first time
                    showLoadingAnimation();
                    isFirstTime = false; // Set flag to false after the first time
                }

                // Execute AsyncTask to perform data retrieval in the background
                new FetchSensorDataTask().execute(snapshot);
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

    // AsyncTask to perform data retrieval in the background
    private class FetchSensorDataTask extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... snapshots) {
            DataSnapshot snapshot = snapshots[0];

            if (snapshot.exists()) {
                // Extract sensor data from snapshot
                Double soilTemp = snapshot.child("Temperature_DS18B20").getValue(Double.class);
                Double airTemp = snapshot.child("Temperature").getValue(Double.class);
                Double humidity = snapshot.child("Humidity").getValue(Double.class);
                Double moisture = snapshot.child("Soil_Moisture").getValue(Double.class);

                // Update UI on the main thread
                runOnUiThread(() -> {
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

                    boolean ventiChecked = snapshot.child("LED_Control").getValue(Boolean.class);

                    if (ventiChecked) {
                        ventiSwitch.setChecked(true);
                    } else {
                        ventiSwitch.setChecked(false);
                    }
                });
            }
            return null;
        }
    }

    // Method to show loading animation for 3 seconds
    private void showLoadingAnimation() {
        // Show loading dialog
        loadingDialogHelper.showDialog("Loading...");

        // Delay dismissal of loading dialog after 1 seconds
        handler.postDelayed(() -> {
            loadingDialogHelper.dismissDialog();
        }, 1000); // 1 second delay
    }

    @Override
    public void onBackPressed() {
// Display confirmation dialog
        DialogHelper.showDialogWithOkCancel(Dashboard.this,
                "Log Out",
                "Are you sure you want to log-out?",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // OK button clicked
                        auth.signOut();

                        // Display success message with your app icon
                        showToastWithAppIcon("Logged Out Successfully", true);

                        // Optionally, redirect user to login activity
                        Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Dashboard.this.startActivity(intent);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Cancel button clicked
                        // Dismiss the dialog (nothing to do here)
                    }
                });
    }

    // Method to show custom toast with app icon
    private void showToastWithAppIcon(String message, boolean isSuccess) {
        LayoutInflater inflater = LayoutInflater.from(Dashboard.this);
        View layout = inflater.inflate(R.layout.toast_layout, null);

        ImageView iconImageView = layout.findViewById(R.id.toast_icon);
        TextView messageTextView = layout.findViewById(R.id.toast_text);

        // Set the app icon based on success or failure
        if (isSuccess) {
            // Set your success icon
            iconImageView.setImageResource(R.drawable.white_wirwo); // Replace with your success icon
        } else {
            // Set your failure icon
            iconImageView.setImageResource(R.drawable.white_wirwo); // Replace with your failure icon
        }

        // Set the message
        messageTextView.setText(message);

        // Create and show the toast
        Toast toast = new Toast(Dashboard.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
