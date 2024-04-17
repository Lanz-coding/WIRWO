package com.allstar.wirwo;

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
import com.google.firebase.database.FirebaseDatabase;

public class DashboardActivity extends Activity implements OnDataChangeListener {

    private FirebaseAuth auth;
    private DatabaseHelper helper;  // Use DatabaseHelper instead of mDatabase

    private SwitchMaterial ventiSwitch;

    private LoadingDialogHelper loadingDialogHelper;

    private Handler handler;

    private TextView soilTempText, airTempText, humidityText, moistureText, welcomeText;
    private ProgressBar soilTempBar, airTempBar, humidityBar, moistureBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

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

        welcomeText = findViewById(R.id.welcome_text);

        // Create an instance of FirebaseDatabase
        FirebaseDatabase database = FirebaseDatabase.getInstance();

// Call the getUsername() method with the database instance and implement the UsernameCallback interface
        DatabaseHelper.getUsername(database, new DatabaseHelper.UsernameCallback() {
            @Override
            public void onUsernameReceived(String username) {
                // Use the retrieved username here
                welcomeText.setText("Ciao, " + username + "! Check your Wireless Worms Today!");

            }
        });

        // Get an instance of DatabaseHelper
        helper = DatabaseHelper.getInstance();

        // Register this activity as a listener for data changes
        helper.addOnDataChangeListener(this);

        // Call method to retrieve initial data
        helper.retrieveInitialData(this);

        // Initialize PopupMenuHelper with context of your activity
        PopupWindowHelper popupMenuHelper = new PopupWindowHelper(this);

        SwitchMaterial waterPumpSwitch = findViewById(R.id.waterPumpSwitch);
        ventiSwitch = findViewById(R.id.ventiSwitch);

        waterPumpSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Water pump switch is turned on
                Toast.makeText(DashboardActivity.this, "Water Pump is turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Water pump switch is turned off
                Toast.makeText(DashboardActivity.this, "Water Pump is turned off", Toast.LENGTH_SHORT).show();
            }
        });

        ventiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Ventilation switch is turned on, send "true" to Firebase
                
                Toast.makeText(DashboardActivity.this, "Ventilation is turned on and LED is lit", Toast.LENGTH_SHORT).show();
            } else {
                // Ventilation switch is turned off, send "false" to Firebase
                Toast.makeText(DashboardActivity.this.getApplicationContext(), "Ventilation is turned off and LED is off", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.toolbar_navigation_icon).setOnClickListener(v -> {
            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister listener to avoid memory leaks
        helper.removeOnDataChangeListener(this);
    }

    @Override
    public void onDatabaseChange(double humidity, boolean ledValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue) {
        // Update UI elements based on the received data
        if (soilTempText != null) {
            soilTempText.setText(String.format("%.1f", tempValue) + "°C");
            soilTempBar.setProgress((int) Math.round(tempValue)); // Assuming progress bar max is 100
        }
        if (airTempText != null) {
            airTempText.setText(String.format("%.1f", airtempValue) + "°C");
            airTempBar.setProgress((int) Math.round(airtempValue)); // Assuming progress bar max is 100
        }
        if (humidityText != null) {
            humidityText.setText(String.format("%.2f", humidity) + "%");
            // Convert humidity to int before setting progress (assuming progress bar max is 100)
            humidityBar.setProgress((int) Math.round(humidity));
        }
        if (moistureText != null) {
            moistureText.setText(String.format("%.2f", moistureValue) + "%");
            // Convert moistureValue to int before setting progress (assuming progress bar max is 100)
            moistureBar.setProgress((int) Math.round(moistureValue));
        }

        if (ventiSwitch != null) {
            ventiSwitch.setChecked(ledValue);
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
        DialogHelper.showDialogWithOkCancel(DashboardActivity.this,
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
                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        DashboardActivity.this.startActivity(intent);
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
        LayoutInflater inflater = LayoutInflater.from(DashboardActivity.this);
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
        Toast toast = new Toast(DashboardActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void updateUIElements(double humidity, boolean ledValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue) {
        // Update UI elements here based on the received data
        if (soilTempText != null) {
            soilTempText.setText(String.format("%.1f", tempValue) + "°C");
            soilTempBar.setProgress((int) Math.round(tempValue)); // Assuming progress bar max is 100
        }
        // ... same logic for other UI elements ...

        if (airTempText != null) {
            airTempText.setText(String.format("%.1f", airtempValue) + "°C");
            airTempBar.setProgress((int) Math.round(airtempValue)); // Assuming progress bar max is 100
        }
        if (humidityText != null) {
            humidityText.setText(String.format("%.2f", humidity) + "%");
            // Convert humidity to int before setting progress (assuming progress bar max is 100)
            humidityBar.setProgress((int) Math.round(humidity));
        }
        if (moistureText != null) {
            moistureText.setText(String.format("%.2f", moistureValue) + "%");
            // Convert moistureValue to int before setting progress (assuming progress bar max is 100)
            moistureBar.setProgress((int) Math.round(moistureValue));
        }

        if (ventiSwitch != null) {
            ventiSwitch.setChecked(ledValue);
        }
    }
}

