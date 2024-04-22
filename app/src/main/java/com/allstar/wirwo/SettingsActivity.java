package com.allstar.wirwo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
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

    private PopupWindowHelper popupMenuHelper;
    private DatabaseHelper helper;

    private LinearLayout faqsLayout, aboutUsLayout;
    private TextView soilTempCurrent, soilMoistureCurrent, humidityCurrent, airTempCurrent, notifCurrent, alertCurrent;

    private DatabaseReference notifSettingsRef, thresholdsRef;

    private SwitchMaterial alertSwitch, notifSwitch;

    private int isNotifsFirstChanged = 0;
    private int isAlertsFirstChanged = 0;



    private FirebaseAuth firebaseAuth;

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


        // Initialize PopupMenuHelper with context of your activity
        popupMenuHelper = new PopupWindowHelper(this);

        notifSwitch = findViewById(R.id.notifSwitch);
        alertSwitch = findViewById(R.id.alertSwitch);

        LinearLayout soilTempThreshold = findViewById(R.id.soil_temperature);
        soilTempCurrent = findViewById(R.id.soilTempCurrent);
        LinearLayout soilMoistureThreshold = findViewById(R.id.soil_moisture);
        soilMoistureCurrent = findViewById(R.id.soilMoistureCurrent);
        LinearLayout humidityThreshold = findViewById(R.id.humidity);
        humidityCurrent = findViewById(R.id.humidityCurrent);
        LinearLayout airTempThreshold = findViewById(R.id.air_temperature);
        airTempCurrent = findViewById(R.id.airTempCurrent);

        notifCurrent = findViewById(R.id.notifCurrent);
        alertCurrent = findViewById(R.id.alertCurrent);

        LinearLayout usernameLayout = findViewById(R.id.username);
        LinearLayout emailLayout = findViewById(R.id.email);
        LinearLayout passwordLayout = findViewById(R.id.password);

        // Initialize the LinearLayout object
        faqsLayout = findViewById(R.id.faqs);
        aboutUsLayout = findViewById(R.id.aboutus);

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


        // Get a reference to the 'notifSettings' node
        thresholdsRef = FirebaseDatabase.getInstance().getReference().child("thresholds");

        // Set an OnClickListener on the LinearLayout
        if (soilTempThreshold != null) {
            soilTempThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showSoilTempThresholdDialog(SettingsActivity.this, new ThresholdDialogHelper.ThresholdDialogCallback() {
                        @Override
                        public void onThresholdSelected(int thresholdValue) {
                            thresholdsRef.child("soilTempThreshold").setValue(thresholdValue)
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
                        public void onThresholdSelected(int thresholdValue) {
                            thresholdsRef.child("soilMoistureThreshold").setValue(thresholdValue)
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
                        public void onThresholdSelected(int thresholdValue) {
                            thresholdsRef.child("humidityThreshold").setValue(thresholdValue)
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
                        public void onThresholdSelected(int thresholdValue) {
                            thresholdsRef.child("airTempThreshold").setValue(thresholdValue)
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

        findViewById(R.id.back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call showPopup() method to show the popup
                popupMenuHelper.showPopup(v);
            }
        });


        // Get a reference to the 'notifSettings' node
        notifSettingsRef = FirebaseDatabase.getInstance().getReference().child("notifSettings");

        // Add a listener to 'allowNotifs' switch
        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Write the value to Firebase Realtime Database
            notifSettingsRef.child("allowNotifs").setValue(isChecked)
                    .addOnSuccessListener(aVoid -> {
                        if (isChecked) {
                            if (isNotifsFirstChanged > 0) {
                                DialogHelper.showDialogWithTitle(SettingsActivity.this, "Notifications", "Notifications successfully turned on.", null);
                            }
                            notifCurrent.setText("Notifications are turned on");
                        } else {
                            DialogHelper.showDialogWithTitle(SettingsActivity.this, "Notifications", "Notifications successfully turned off.", null);
                            notifCurrent.setText("Notifications are turned off");
                        }
                        // Increment after setting the value
                        isNotifsFirstChanged += 1;
                    })
                    .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to update allowNotifs value", Toast.LENGTH_SHORT).show());
        });

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
                                 double soilTempThresh, double soilMoistureThresh, double humidityThresh, double airTempThresh) {

        if (soilTempCurrent != null) {
            soilTempCurrent.setText("Current Threshold: " + String.format("%.0f", soilTempThresh) + "°C");
        }
        if (soilMoistureCurrent != null) {
            soilMoistureCurrent.setText("Current Threshold: " +  String.format("%.0f", soilMoistureThresh) + "%");
        }
        if (humidityCurrent != null) {
            humidityCurrent.setText("Current Threshold: " +  String.format("%.0f", humidityThresh) + "%");
        }
        if (airTempCurrent != null) {
            airTempCurrent.setText("Current Threshold: " +  String.format("%.0f", airTempThresh) + "°C");
        }
        // Update UI elements based on the retrieved values
        if (notifSwitch != null) {
            // Update UI elements according to allowNotifs value
            // Example:
            notifSwitch.setChecked(notifsValue);
        }

        if (alertSwitch != null) {
            // Update UI elements according to allowAlerts value
            // Example:
            alertSwitch.setChecked(alertsValue);
        }

    }

    // Method to fetch data from Firebase and update UI
    void fetchDataAndUpdateUI(boolean alertsValue, boolean notifsValue,
                              double soilTempThresh, double soilMoistureThresh, double humidityThresh, double airTempThresh) {
        if (soilTempCurrent != null) {
            soilTempCurrent.setText("Current Threshold: " + String.format("%.0f", soilTempThresh) + "°C");
        }
        if (soilMoistureCurrent != null) {
            soilMoistureCurrent.setText("Current Threshold: " +  String.format("%.0f", soilMoistureThresh) + "%");
        }
        if (humidityCurrent != null) {
            humidityCurrent.setText("Current Threshold: " +  String.format("%.0f", humidityThresh) + "%");
        }
        if (airTempCurrent != null) {
            airTempCurrent.setText("Current Threshold: " +  String.format("%.0f", airTempThresh) + "°C");
        }
        // Update UI elements based on the retrieved values
        if (notifSwitch != null) {
            // Update UI elements according to allowNotifs value
            // Example:
            notifSwitch.setChecked(notifsValue);
        }

        if (alertSwitch != null) {
            // Update UI elements according to allowAlerts value
            // Example:
            alertSwitch.setChecked(alertsValue);
        }
    }

}
