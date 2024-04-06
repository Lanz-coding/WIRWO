package com.allstar.wirwo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends Activity {

    private PopupWindowHelper popupMenuHelper;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize PopupMenuHelper with context of your activity
        popupMenuHelper = new PopupWindowHelper(this);

        findViewById(R.id.toolbar_navigation_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call showPopup() method to show the popup
                popupMenuHelper.showPopup(v);
            }
        });

        // Soil Temperature SeekBar and TextView
        SeekBar soilTempSeekBar = findViewById(R.id.soil_temp_seekbar);
        final TextView soilTempProgressText = findViewById(R.id.soiltemp_thresh_text);

        soilTempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                soilTempProgressText.setText(String.valueOf(progress) + "°C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }
        });

        // Soil Moisture SeekBar and TextView
        SeekBar soilMoistureSeekBar = findViewById(R.id.soil_moisture_seekbar);
        final TextView soilMoistureProgressText = findViewById(R.id.soilmoisture_thresh_text);

        soilMoistureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                soilMoistureProgressText.setText(String.valueOf(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }
        });

        // Humidity SeekBar and TextView
        SeekBar humiditySeekBar = findViewById(R.id.humidity_seekbar);
        final TextView humidityProgressText = findViewById(R.id.humidity_thresh_text);

        humiditySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                humidityProgressText.setText(String.valueOf(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }
        });

        // Air Temperature SeekBar and TextView
        SeekBar airTempSeekBar = findViewById(R.id.airtemp_seekbar);
        final TextView airTempProgressText = findViewById(R.id.airtemp_thresh_text);

        airTempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                airTempProgressText.setText(String.valueOf(progress) + "°C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed for this implementation
            }
        });

        SwitchMaterial notifSwitch = findViewById(R.id.notifSwitch);
        SwitchMaterial alertSwitch = findViewById(R.id.alertSwitch);

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
        LinearLayout faqsLayout = findViewById(R.id.faqs_layout);

        faqsLayout.setOnClickListener(v -> {
            try {
                // Handle button1 click
                Intent intent = new Intent(SettingsActivity.this, faqs.class);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Handle the exception appropriately, e.g., show an error message
                Toast.makeText(SettingsActivity.this, "Error starting FAQs activity", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
