package com.allstar.wirwo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

public class SettingsActivity extends Activity implements OnDataChangeListener {

    private AlertsDialogHelper alertsDialogHelper;
    private PopupWindowHelper popupMenuHelper;
    private DatabaseHelper helper;

    private LinearLayout resetLayout, faqsLayout, aboutUsLayout;
    private TextView soilTempCurrent, soilMoistureCurrent, humidityCurrent, airTempCurrent, alertCurrent;

    private DatabaseReference notifSettingsRef, thresholdsRef;

    private SwitchMaterial alertSwitch, notifSwitch;

    private int isNotifsFirstChanged = 0;
    private int isAlertsFirstChanged = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get an instance of DatabaseHelper
        helper = DatabaseHelper.getInstance();

        // Register this activity as a listener for data changes
        helper.addOnDataChangeListener(this);

        // Call method to retrieve initial data
        helper.retrieveSettingsInitialData(this);

        // Initialize the AlertsDialogHelper
        alertsDialogHelper = new AlertsDialogHelper(this);

        // Add the AlertsDialogHelper as a listener for database changes
        DatabaseHelper.getInstance().addOnDataChangeListener(alertsDialogHelper::onDatabaseChange);

        // Initialize PopupMenuHelper with context of your activity
        popupMenuHelper = new PopupWindowHelper(this);

        alertSwitch = findViewById(R.id.alertSwitch);

        LinearLayout soilTempThreshold = findViewById(R.id.soil_temperature);
        soilTempCurrent = findViewById(R.id.soilTempCurrent);
        LinearLayout soilMoistureThreshold = findViewById(R.id.soil_moisture);
        soilMoistureCurrent = findViewById(R.id.soilMoistureCurrent);
        LinearLayout humidityThreshold = findViewById(R.id.humidity);
        humidityCurrent = findViewById(R.id.humidityCurrent);
        LinearLayout airTempThreshold = findViewById(R.id.air_temperature);
        airTempCurrent = findViewById(R.id.airTempCurrent);

        resetLayout = findViewById(R.id.resetLayout);

        alertCurrent = findViewById(R.id.alertCurrent);

        LinearLayout usernameLayout = findViewById(R.id.username);
        LinearLayout emailLayout = findViewById(R.id.email);
        LinearLayout passwordLayout = findViewById(R.id.password);

        // Initialize the LinearLayout object
        faqsLayout = findViewById(R.id.faqs);
        aboutUsLayout = findViewById(R.id.aboutus);


        resetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogHelper.showDialogWithOkCancel(SettingsActivity.this,
                        "Reset Threshold Values",
                        "Are you sure to reset Threshold Values?",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // OK button clicked
                                thresholdsRef.child("soilMoistureThreshold").child("min").setValue(50);
                                thresholdsRef.child("soilMoistureThreshold").child("max").setValue(90);
                                thresholdsRef.child("soilTempThreshold").child("min").setValue(20);
                                thresholdsRef.child("soilTempThreshold").child("max").setValue(30);
                                thresholdsRef.child("humidityThreshold").child("min").setValue(60);
                                thresholdsRef.child("humidityThreshold").child("max").setValue(80);
                                thresholdsRef.child("airTempThreshold").child("min").setValue(20);
                                thresholdsRef.child("airTempThreshold").child("max").setValue(32)
                                        .addOnSuccessListener(aVoid -> {
                                            DialogHelper.showDialogWithTitle(SettingsActivity.this, "Threshold Updated.", "", null);
                                            // Fetch data and update UI after setting the threshold
                                        })
                                        .addOnFailureListener(e -> {
                                            DialogHelper.showDialogWithTitle(SettingsActivity.this, "Error Updating Threshold.", "", null);
                                        });
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Cancel button clicked
                                // Dismiss the dialog (nothing to do here)
                            }
                        });

            }
        });
        // Create an instance of FirebaseDatabase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usernameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper.getUsername(database, new DatabaseHelper.UsernameCallback() {
                    @Override
                    public void onUsernameReceived(String username) {
                        try {
                            // Handle button1 click
                            Intent intent = new Intent(SettingsActivity.this, ChangeUsernameActivity.class);
                            SettingsActivity.this.startActivity(intent);

                        } catch (ActivityNotFoundException e) {
                            // Handle the exception appropriately, e.g., show an error message
                            Toast.makeText(SettingsActivity.this, "Error starting Change Username's activity", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });

        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                DialogHelper.showDialogWithTitle(SettingsActivity.this, "Email", user.getEmail(), null);

            }
        });

        passwordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Handle button1 click
                    Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                    SettingsActivity.this.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(SettingsActivity.this, "Error starting Change Password's activity", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Get a reference to the 'thresholds' node
        thresholdsRef = FirebaseDatabase.getInstance().getReference().child("thresholds");

        // Set an OnClickListener on the LinearLayout
        if (soilTempThreshold != null) {
            soilTempThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showSoilTempThresholdDialog(SettingsActivity.this, new ThresholdDialogHelper.ThresholdDialogCallback() {
                        @Override
                        public void onThresholdSelected(int minThresholdValue, int maxThresholdValue) {
                            thresholdsRef.child("soilTempThreshold").child("min").setValue(minThresholdValue);
                            thresholdsRef.child("soilTempThreshold").child("max").setValue(maxThresholdValue)
                                    .addOnSuccessListener(aVoid -> {
                                        DialogHelper.showDialogWithTitle(SettingsActivity.this, "Threshold Updated.", "", null);
                                        // Fetch data and update UI after setting the threshold
                                    })
                                    .addOnFailureListener(e -> {
                                        DialogHelper.showDialogWithTitle(SettingsActivity.this, "Error Updating Threshold.", "", null);
                                    });
                        }
                    });
                }
            });
        }

        if (soilMoistureThreshold != null) {
            soilMoistureThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showSoilMoistureThresholdDialog(SettingsActivity.this, new ThresholdDialogHelper.ThresholdDialogCallback() {
                        @Override
                        public void onThresholdSelected(int minThresholdValue, int maxThresholdValue) {
                            thresholdsRef.child("soilMoistureThreshold").child("min").setValue(minThresholdValue);
                            thresholdsRef.child("soilMoistureThreshold").child("max").setValue(maxThresholdValue)
                                    .addOnSuccessListener(aVoid -> {
                                        DialogHelper.showDialogWithTitle(SettingsActivity.this, "Threshold Updated.", "", null);
                                        // Fetch data and update UI after setting the threshold
                                    })
                                    .addOnFailureListener(e -> {
                                        DialogHelper.showDialogWithTitle(SettingsActivity.this, "Error Updating Threshold.", "", null);
                                    });
                        }
                    });
                }
            });
        }

        if (humidityThreshold != null) {
            humidityThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showHumidityThresholdDialog(SettingsActivity.this, new ThresholdDialogHelper.ThresholdDialogCallback() {
                        @Override
                        public void onThresholdSelected(int minThresholdValue, int maxThresholdValue) {
                            thresholdsRef.child("humidityThreshold").child("min").setValue(minThresholdValue);
                            thresholdsRef.child("humidityThreshold").child("max").setValue(maxThresholdValue)
                                    .addOnSuccessListener(aVoid -> {
                                        DialogHelper.showDialogWithTitle(SettingsActivity.this, "Threshold Updated.", "", null);
                                        // Fetch data and update UI after setting the threshold
                                    })
                                    .addOnFailureListener(e -> {
                                        DialogHelper.showDialogWithTitle(SettingsActivity.this, "Error Updating Threshold.", "", null);
                                    });
                        }
                    });
                }
            });
        }


        if (airTempThreshold != null) {
            airTempThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showAirTempThresholdDialog(SettingsActivity.this, new ThresholdDialogHelper.ThresholdDialogCallback() {
                        @Override
                        public void onThresholdSelected(int minThresholdValue, int maxThresholdValue) {
                            thresholdsRef.child("airTempThreshold").child("min").setValue(minThresholdValue);
                            thresholdsRef.child("airTempThreshold").child("max").setValue(maxThresholdValue)
                                    .addOnSuccessListener(aVoid -> {
                                        DialogHelper.showDialogWithTitle(SettingsActivity.this, "Threshold Updated.", "", null);
                                        // Fetch data and update UI after setting the threshold
                                    })
                                    .addOnFailureListener(e -> {
                                        DialogHelper.showDialogWithTitle(SettingsActivity.this, "Error Updating Threshold.", "", null);
                                    });
                        }
                    });
                }
            });
        }


        // Set an OnClickListener on the LinearLayout
        if (faqsLayout != null) {
            faqsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // Handle button1 click
                        Intent intent = new Intent(SettingsActivity.this, FAQsActivity.class);
                        SettingsActivity.this.startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        // Handle the exception appropriately, e.g., show an error message
                        Toast.makeText(SettingsActivity.this, "Error starting FAQs's activity", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (aboutUsLayout != null) {
            aboutUsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // Handle button1 click
                        Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
                        SettingsActivity.this.startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        // Handle the exception appropriately, e.g., show an error message
                        Toast.makeText(SettingsActivity.this, "Error starting FAQs's activity", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        findViewById(R.id.back_icon).setOnClickListener(v -> {
            // Get the coordinates of the toolbar navigation icon
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            int xOffset = location[0]; // x coordinate
            int yOffset = location[1]; // y coordinate plus the height of the icon

            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v, xOffset, yOffset);
        });


        // Get a reference to the 'notifSettings' node
        notifSettingsRef = FirebaseDatabase.getInstance().getReference().child("notifSettings");

        // Add a listener to 'allowAlerts' switch
        alertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Write the value to Firebase Realtime Database
            notifSettingsRef.child("allowAlerts").setValue(isChecked)
                    .addOnSuccessListener(aVoid -> {
                        if (isChecked) {
                            if (isAlertsFirstChanged > 0) {
                                DialogHelper.showDialogWithTitle(SettingsActivity.this, "Alerts", "Alerts successfully turned on.", null);
                            }
                            alertCurrent.setText("In-App Alerts are turned on");
                        } else {
                            DialogHelper.showDialogWithTitle(SettingsActivity.this, "Alerts", "Alerts successfully turned off.", null);
                            alertCurrent.setText("In-App Alerts are turned off");
                        }
                        // Increment after setting the value
                        isAlertsFirstChanged += 1;
                    })
                    .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to update allowAlerts value", Toast.LENGTH_SHORT).show());
        });

        // Add a listener to database changes to update UI
        notifSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Update UI with latest data

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancelled event
            }
        });
    }


    @Override
    public void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue,
                                 double minSoilTempThresh, double maxSoilTempThresh,
                                 double minSoilMoistureThresh, double maxSoilMoistureThresh, double minHumidityThresh, double maxHumidityThresh,
                                 double minAirTempThresh, double maxAirTempThresh) {

        Log.d("DatabaseChange", "Received minSoilTempThresh: " + minSoilTempThresh);
        Log.d("DatabaseChange", "Received maxSoilTempThresh: " + maxSoilTempThresh);
        Log.d("DatabaseChange", "Received minSoilMoistureThresh: " + minSoilMoistureThresh);
        Log.d("DatabaseChange", "Received maxSoilMoistureThresh: " + maxSoilMoistureThresh);
        Log.d("DatabaseChange", "Received minHumidityThresh: " + minHumidityThresh);
        Log.d("DatabaseChange", "Received maxHumidityThresh: " + maxHumidityThresh);
        Log.d("DatabaseChange", "Received minAirTempThresh: " + minAirTempThresh);
        Log.d("DatabaseChange", "Received maxAirTempThresh: " + maxAirTempThresh);

        double minSoilTemp = DatabaseHelper.getMinSoilTempThreshold();
        double maxSoilTemp = DatabaseHelper.getMaxSoilTempThreshold();
        double minSoilMoisture = DatabaseHelper.getMinSoilMoistureThreshold();
        double maxSoilMoisture = DatabaseHelper.getMaxSoilMoistureThreshold();
        double minHumidity = DatabaseHelper.getMinHumidityThreshold();
        double maxHumidity = DatabaseHelper.getMaxHumidityThreshold();
        double minAirTemp = DatabaseHelper.getMinAirTempThreshold();
        double maxAirTemp = DatabaseHelper.getMaxAirTempThreshold();

        // Update UI elements based on the retrieved values
        if (soilTempCurrent != null) {
            soilTempCurrent.setText("Current Threshold Range: " + String.format("%.0f", minSoilTemp) + "°C - " + String.format("%.0f", maxSoilTemp) + "°C");
        }
        if (soilMoistureCurrent != null) {
            soilMoistureCurrent.setText("Current Threshold Range: " + String.format("%.0f", minSoilMoisture) + "% - " + String.format("%.0f", maxSoilMoisture) + "%");
        }
        if (humidityCurrent != null) {
            humidityCurrent.setText("Current Threshold Range: " + String.format("%.0f", minHumidity) + "% - " + String.format("%.0f", maxHumidity) + "%");
        }
        if (airTempCurrent != null) {
            airTempCurrent.setText("Current Threshold Range: " + String.format("%.0f", minAirTemp) + "°C - " + String.format("%.0f", maxAirTemp) + "°C");
        }

        // Update Switches based on the retrieved values
        if (notifSwitch != null) {
            notifSwitch.setChecked(notifsValue);
        }

        if (alertSwitch != null) {
            alertSwitch.setChecked(alertsValue);
        }
    }


    // Method to fetch data from Firebase and update UI
    void fetchDataAndUpdateUI(boolean alertsValue, boolean notifsValue,
                              double minSoilTempThresh, double maxSoilTempThresh, double minSoilMoistureThresh, double maxSoilMoistureThresh,
                              double minHumidityThresh, double maxHumidityThresh, double minAirTempThresh, double maxAirTempThresh) {
        // Call onDatabaseChange with the fetched data
        double humidityValue = 0;
        boolean ventiValue = false;
        boolean waterValue = false;
        double moistureValue = 0;
        double tempValue = 0;
        double airTempValue = 0;
        onDatabaseChange(humidityValue, ventiValue, waterValue, moistureValue, tempValue, airTempValue, alertsValue, notifsValue,
                minSoilTempThresh, maxSoilTempThresh, minSoilMoistureThresh, maxSoilMoistureThresh,
                minHumidityThresh, maxHumidityThresh, minAirTempThresh, maxAirTempThresh);
    }

}