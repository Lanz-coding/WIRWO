package com.allstar.wirwo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends Activity {

    private PopupWindowHelper popupMenuHelper;

    private LinearLayout faqsLayout, aboutUsLayout;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize PopupMenuHelper with context of your activity
        popupMenuHelper = new PopupWindowHelper(this);
        SwitchMaterial notifSwitch = findViewById(R.id.notifSwitch);
        SwitchMaterial alertSwitch = findViewById(R.id.alertSwitch);

        LinearLayout soilTempThreshold = findViewById(R.id.soil_temperature);
        TextView soilTempCurrent = findViewById(R.id.soilTempCurrent);
        LinearLayout soilMoistureThreshold = findViewById(R.id.soil_moisture);
        TextView soilMoistureCurrent = findViewById(R.id.soilMoistureCurrent);
        LinearLayout humidityThreshold = findViewById(R.id.humidity);
        TextView humidityCurrent = findViewById(R.id.humidityCurrent);
        LinearLayout airTempThreshold = findViewById(R.id.air_temperature);
        TextView airTempCurrent = findViewById(R.id.airTempCurrent);

        // Initialize the LinearLayout object
        faqsLayout = findViewById(R.id.faqs);
        aboutUsLayout = findViewById(R.id.aboutus);

        // Set an OnClickListener on the LinearLayout
        if (soilTempThreshold != null) {
            soilTempThreshold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThresholdDialogHelper.showSoilTempThresholdDialog(SettingsActivity.this, soilTempCurrent, null, null);
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



        notifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Water pump switch is turned on
                Toast.makeText(SettingsActivity.this, "Notifications are turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Water pump switch is turned off
                Toast.makeText(SettingsActivity.this, "Notifications are turned off", Toast.LENGTH_SHORT).show();
            }
        });

        alertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Water pump switch is turned on
                Toast.makeText(SettingsActivity.this, "Alerts are turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Water pump switch is turned off
                Toast.makeText(SettingsActivity.this, "Alerts are turned off", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
