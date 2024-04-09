package com.allstar.wirwo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.switchmaterial.SwitchMaterial;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.atomic.AtomicBoolean;


public class SettingsActivity extends Activity {

    private PopupWindowHelper popupMenuHelper;

    private LinearLayout faqsLayout, aboutUsLayout;
    private TextView soilTempCurrent, soilMoistureCurrent, humidityCurrent, airTempCurrent, notifCurrent, alertCurrent;

    private DatabaseReference notifSettingsRef, thresholdsRef;

    private SwitchMaterial alertSwitch, notifSwitch;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

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

        ImageView refreshButton = findViewById(R.id.refresh_btn);

        notifCurrent = findViewById(R.id.notifCurrent);
        alertCurrent = findViewById(R.id.alertCurrent);

        // Initialize the LinearLayout object
        faqsLayout = findViewById(R.id.faqs);
        aboutUsLayout = findViewById(R.id.aboutus);

        // Fetch data and update UI initially
        fetchDataAndUpdateUI();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call fetchDataAndUpdateUI() to fetch data from Firebase and update UI
                fetchDataAndUpdateUI();
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
                                        Toast.makeText(SettingsActivity.this, "Threshold Updated", Toast.LENGTH_SHORT).show();
                                        // Fetch data and update UI after setting the threshold
                                        fetchDataAndUpdateUI();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to update soil temperature threshold", Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            });
        }


        if (soilMoistureThreshold != null) {
            soilMoistureThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showSoilMoistureThresholdDialog(SettingsActivity.this, soilMoistureCurrent, null, null);
                }
            });
        }

        if (humidityThreshold != null) {
            humidityThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showHumidityThresholdDialog(SettingsActivity.this, humidityCurrent, null, null);
                }
            });
        }

        if (airTempThreshold != null) {
            airTempThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showAirTempThresholdDialog(SettingsActivity.this, airTempCurrent,null, null);
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

        findViewById(R.id.toolbar_navigation_icon).setOnClickListener(new View.OnClickListener() {
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
                            notifCurrent.setText("Notifications are turned on");
                            Toast.makeText(SettingsActivity.this, "Notifications enabled", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Notifications disabled", Toast.LENGTH_SHORT).show();
                            notifCurrent.setText("Notifications are turned off");
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to update allowNotifs value", Toast.LENGTH_SHORT).show());
        });

// Add a listener to 'allowAlerts' switch
        alertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Write the value to Firebase Realtime Database
            notifSettingsRef.child("allowAlerts").setValue(isChecked)
                    .addOnSuccessListener(aVoid -> {
                        if (isChecked) {
                            Toast.makeText(SettingsActivity.this, "Alerts enabled", Toast.LENGTH_SHORT).show();
                            alertCurrent.setText("In-App Alerts are turned on");
                        } else {
                            Toast.makeText(SettingsActivity.this, "Alerts disabled", Toast.LENGTH_SHORT).show();
                            alertCurrent.setText("In-App Alerts are turned off");
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Failed to update allowAlerts value", Toast.LENGTH_SHORT).show());
        });

// Add a listener to database changes to update UI
        notifSettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Update UI with latest data
                fetchDataAndUpdateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancelled event
            }
        });
    }

    // Method to fetch data from Firebase and update UI
    private void fetchDataAndUpdateUI() {
        // Get a reference to the root node
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        // Add a listener to retrieve the data
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Call FetchSensorDataTask to retrieve data from the snapshot
                new FetchSettingsDataTask().execute(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancelled event
            }
        });
    }

    private class FetchSettingsDataTask extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... snapshots) {
            DataSnapshot snapshot = snapshots[0];

            if (snapshot.exists()) {
                Log.d("FirebaseSnapshot", snapshot.toString());

                Integer soilTempValue = snapshot.child("thresholds").child("soilTempThreshold").getValue(Integer.class);
                Log.d("SoilTempValue", "Value: " + soilTempValue);
                // int soilMoistureValue = snapshot.child("threshold").child("soilMoistureThreshold").getValue(Integer.class);
                // int humidityValue = snapshot.child("threshold").child("HumidityThreshold").getValue(Integer.class);
                // airTempValue = snapshot.child("threshold").child("airTempThreshold").getValue(Integer.class);
                // Extract allowNotifs and allowAlerts from notifSettings node
                Boolean allowNotifs = snapshot.child("notifSettings").child("allowNotifs").getValue(Boolean.class);
                Boolean allowAlerts = snapshot.child("notifSettings").child("allowAlerts").getValue(Boolean.class);

                // Update UI on the main thread
                runOnUiThread(() -> {

                    if (soilTempValue != null) {
                        soilTempCurrent.setText("Current Threshold: " + String.valueOf(soilTempValue));
                    }
                    // Update UI elements based on the retrieved values
                    if (allowNotifs != null) {
                        // Update UI elements according to allowNotifs value
                        // Example:
                        notifSwitch.setChecked(allowNotifs);
                    }

                    if (allowAlerts != null) {
                        // Update UI elements according to allowAlerts value
                        // Example:
                        alertSwitch.setChecked(allowAlerts);
                    }
                });
            }
            return null;
        }
    }

}
