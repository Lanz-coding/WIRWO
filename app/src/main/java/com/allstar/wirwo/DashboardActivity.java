package com.allstar.wirwo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;


import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.juanarton.arcprogressbar.ArcProgressBar;

public class DashboardActivity extends Activity implements OnDataChangeListener {
    private AlertsDialogHelper alertsDialogHelper;
    private FirebaseAuth auth;
    private DatabaseHelper helper;

    private SwitchMaterial ventiSwitch, waterSwitch;

    private LoadingDialogHelper loadingDialogHelper;

    private Handler handler;

    private TextView soilTempText, airTempText, humidityText, moistureText, welcomeText;
    private ProgressBar soilTempProgressBar;
    private ProgressBar airTempProgressBar;
    private ArcProgressBar humidityProgressBar;
    private ArcProgressBar soilMoistureProgressBar;

    private LinearLayout dataAnalytics, history, settings;

    private int isWaterPumpFirstChanged = 0;
    private int isVentiFirstChanged = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Check for internet connection
        if (!isNetworkAvailable()) {
            // Show dialog indicating no internet connection
            DialogHelper.showNoIntenetDialog(DashboardActivity.this);
        }

        // Initialize the AlertsDialogHelper
        alertsDialogHelper = new AlertsDialogHelper(this);

        // Add the AlertsDialogHelper as a listener for database changes
        DatabaseHelper.getInstance().addOnDataChangeListener(alertsDialogHelper::onDatabaseChange);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Initialize the LoadingDialogHelper
        loadingDialogHelper = new LoadingDialogHelper(this);
        handler = new Handler();

        // Get references to TextViews and ProgressBars
        soilTempText = findViewById(R.id.temp_soil);
        soilTempProgressBar = findViewById(R.id.soil_temp_progress_bar);
        airTempText = findViewById(R.id.temp_air);
        airTempProgressBar = findViewById(R.id.air_temp_progress_bar);
        humidityText = findViewById(R.id.humidity_value);
        humidityProgressBar = findViewById(R.id.humidity_progress_bar);
        moistureText = findViewById(R.id.moisture_value);
        soilMoistureProgressBar = findViewById(R.id.moisture_progress_bar);

        dataAnalytics = findViewById(R.id.dashboard_to_analytics);
        history = findViewById(R.id.dashboard_to_history);
        settings = findViewById(R.id.dashboard_to_settings);

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

        dataAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, DataAnalyticsActivity.class);
                DashboardActivity.this.startActivity(intent);
            }
        });


        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, HistoryActivity.class);
                DashboardActivity.this.startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                DashboardActivity.this.startActivity(intent);
            }
        });

        // Get an instance of DatabaseHelper
        helper = DatabaseHelper.getInstance();

        // Register this activity as a listener for data changes
        helper.addOnDataChangeListener(this);

        // Call method to retrieve initial data
        helper.retrieveDashboardInitialData(this);

        // Initialize PopupMenuHelper with context of your activity
        PopupWindowHelper popupMenuHelper = new PopupWindowHelper(this);

        // Initialize water switch
        waterSwitch = findViewById(R.id.waterPumpSwitch);
        waterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle water switch state change
            if (isChecked) {
                // Water pump switch is turned on
                helper.setWaterValue(isChecked);
                if (isWaterPumpFirstChanged > 0) {
                    DialogHelper.showDialogWithTitle(DashboardActivity.this, "SUCCESS", "Water Pump is turned on", null);
                }
            } else {
                // Water pump switch is turned off
                helper.setWaterValue(false);
                if (isWaterPumpFirstChanged > 0) {
                    DialogHelper.showDialogWithTitle(DashboardActivity.this, "SUCCESS", "Water Pump is turned off", null);
                }
            }
            // Increment the counter after the first state change
            isWaterPumpFirstChanged += 1;
        });

        // Initialize ventilation switch
        ventiSwitch = findViewById(R.id.ventiSwitch);
        ventiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle ventilation switch state change
            if (isChecked) {
                // Ventilation switch is turned on
                helper.setVentiValue(isChecked);
                if (isVentiFirstChanged > 0) {
                    DialogHelper.showDialogWithTitle(DashboardActivity.this, "SUCCESS", "Ventilation is turned on", null);
                }
            } else {
                // Ventilation switch is turned off
                helper.setVentiValue(false);
                if (isVentiFirstChanged > 0) {
                    DialogHelper.showDialogWithTitle(DashboardActivity.this, "SUCCESS", "Ventilation is turned off", null);
                }
            }
            // Increment the counter after the first state change
            isVentiFirstChanged += 1;
        });

        findViewById(R.id.back_icon).setOnClickListener(v -> {
            // Get the coordinates of the toolbar navigation icon
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            int xOffset = location[0]; // x coordinate
            int yOffset = location[1]; // y coordinate plus the height of the icon

            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v, xOffset, yOffset);
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister listener to avoid memory leaks
        helper.removeOnDataChangeListener(this);
        DatabaseHelper.getInstance().removeOnDataChangeListener(alertsDialogHelper::onDatabaseChange);
    }

    public void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue,
                                 double minSoilTempThresh, double maxSoilTempThresh,
                                 double minSoilMoistureThresh, double maxSoilMoistureThresh,
                                 double minHumidityThresh, double maxHumidityThresh,
                                 double minAirTempThresh, double maxAirTempThresh) {
        // Initialize default color
        int defaultColor = ContextCompat.getColor(DashboardActivity.this, R.color.lighterGreen);
        ColorStateList defaultColorStateList = ColorStateList.valueOf(defaultColor);

        // Initialize ColorStateList for each progress bar
        ColorStateList soilTempColorStateList = defaultColorStateList;
        ColorStateList airTempColorStateList = defaultColorStateList;
        ColorStateList humidityColorStateList = defaultColorStateList;
        ColorStateList moistureColorStateList = defaultColorStateList;

        SaveToDB.saveUserData(tempValue, moistureValue, humidity, airtempValue);

        // Update UI elements based on the received data
        if (soilTempText != null) {
            soilTempText.setText(String.format("%.1f", tempValue) + "°C");
            soilTempProgressBar.setProgress((int) Math.round(tempValue)); // Assuming progress bar max is 100

            // Change color based on thresholds
            if (tempValue >= maxSoilTempThresh) {
                soilTempColorStateList = ColorStateList.valueOf(Color.parseColor("#F44336"));
            } else if (tempValue >= maxSoilTempThresh - 3) { // Adjust threshold for orange color
                soilTempColorStateList = ColorStateList.valueOf(Color.parseColor("#FFAD00")); // Orange color
            } else if (tempValue <= minSoilTempThresh) {
                soilTempColorStateList = ColorStateList.valueOf(Color.parseColor("#03A9F4")); // Or any color for minimum threshold
            } else if (tempValue <= minSoilTempThresh + 3) { // Adjust threshold for a different color
                soilTempColorStateList = ColorStateList.valueOf(Color.parseColor("#ADD8E6")); // Yellow color for nearing minimum threshold
            }

            // Set the progress tint list
            soilTempProgressBar.setProgressTintList(soilTempColorStateList);

        }

        if (airTempText != null) {
            airTempText.setText(String.format("%.1f", airtempValue) + "°C");
            airTempProgressBar.setProgress((int) Math.round(airtempValue)); // Assuming progress bar max is 100

            // Change color based on thresholds
            if (airtempValue > maxAirTempThresh) {
                airTempColorStateList = ColorStateList.valueOf(Color.parseColor("#F44336")); // Red
            } else if (airtempValue > maxAirTempThresh - 3) { // Adjust threshold for orange color
                airTempColorStateList = ColorStateList.valueOf(Color.parseColor("#FFAD00")); // Orange
            } else if (airtempValue < minAirTempThresh) {
                airTempColorStateList = ColorStateList.valueOf(Color.parseColor("#03A9F4")); // Blue
            } else if (airtempValue < minAirTempThresh + 5) { // Adjust threshold for a different color
                airTempColorStateList = ColorStateList.valueOf(Color.parseColor("#ADD8E6")); // Light Blue
            }

            // Set the progress tint list
            airTempProgressBar.setProgressTintList(airTempColorStateList);
        }

        if (humidityText != null) {
            humidityText.setText(String.format("%.2f", humidity) + "%");
            // Convert humidity to int before setting progress (assuming progress bar max is 100)
            humidityProgressBar.setProgress((int) Math.round(humidity));

            // Change color based on thresholds
            int progressColor;
            if (moistureValue > maxSoilMoistureThresh) {
                progressColor = Color.parseColor("#F44336");
            } else if (moistureValue > maxSoilMoistureThresh - 3) { // Adjust threshold for orange color
                progressColor = Color.parseColor("#FFAD00"); // Orange color
            } else if (moistureValue < minSoilMoistureThresh) {
                progressColor = Color.parseColor("#03A9F4"); // Or any color for minimum threshold
            } else if (moistureValue < minSoilMoistureThresh + 3) { // Adjust threshold for a different color
                progressColor = Color.parseColor("#ADD8E6"); // Yellow color for nearing minimum threshold
            } else {
                // Default color when no conditions match
                progressColor = Color.parseColor("#4c6444"); // Default white color
            }

            // Set the progress tint list
            humidityProgressBar.setProgressColor(progressColor);
        }

        if (moistureText != null) {
            moistureText.setText(String.format("%.2f", moistureValue) + "%");
            // Convert moistureValue to int before setting progress (assuming progress bar max is 100)
            soilMoistureProgressBar.setProgress((int) Math.round(moistureValue));

            // Change color based on thresholds
            int progressColor;
            if (humidity > maxHumidityThresh) {
                progressColor = Color.parseColor("#F44336");
            } else if (humidity > maxHumidityThresh - 3) { // Adjust threshold for orange color
                progressColor = Color.parseColor("#FFAD00"); // Orange color
            } else if (humidity < minHumidityThresh) {
                progressColor = Color.parseColor("#03A9F4"); // Or any color for minimum threshold
            } else if (humidity < minHumidityThresh + 3) { // Adjust threshold for a different color
                progressColor = Color.parseColor("#ADD8E6"); // Yellow color for nearing minimum threshold
            } else {
                // Default color when no conditions match
                progressColor = Color.parseColor("#4c6444"); // Default white color
            }

            // Set the progress tint list
            soilMoistureProgressBar.setProgressColor(progressColor);
        }

        // Set switch states
        if (ventiSwitch != null) {

            ventiSwitch.setChecked(ventiValue);
        }

        if (waterSwitch != null) {
            waterSwitch.setChecked(waterValue);
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

    // Method to update UI elements based on data received from the database
    public void updateUIElements(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue,
                                 double minSoilTempThresh, double maxSoilTempThresh,
                                 double minSoilMoistureThresh, double maxSoilMoistureThresh,
                                 double minHumidityThresh, double maxHumidityThresh,
                                 double minAirTempThresh, double maxAirTempThresh) {
        // Call onDatabaseChange with the provided parameters
        onDatabaseChange(humidity, ventiValue, waterValue, moistureValue, tempValue, airtempValue, alertsValue, notifsValue,
                minSoilTempThresh, maxSoilTempThresh,
                minSoilMoistureThresh, maxSoilMoistureThresh,
                minHumidityThresh, maxHumidityThresh,
                minAirTempThresh, maxAirTempThresh);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
