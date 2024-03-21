package com.example.wirwo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class Dashboard extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);


        // Retrieve views using their IDs
        ImageView toolbarNavigationIcon = findViewById(R.id.toolbar_navigation_icon);
        ImageView overlayImage = findViewById(R.id.overlay_image);
        Switch waterPumpSwitch = findViewById(R.id.waterPumpSwitch);
        Switch ventiSwitch = findViewById(R.id.ventiSwitch);
        TextView dashboardTitle = findViewById(R.id.dashboard_title);
        TextView soiltempText = findViewById(R.id.soiltemp_text);
        TextView moistureText = findViewById(R.id.moisture_text);
        TextView humidityText = findViewById(R.id.humidity_text);
        TextView airtempText = findViewById(R.id.airtemp_text);
        ProgressBar soiltempProgressBar = findViewById(R.id.soiltemp_bar);
        ProgressBar moistureProgressBar = findViewById(R.id.moisture_bar);
        ProgressBar humidityProgressBar = findViewById(R.id.humidity_bar);
        ProgressBar airtempProgressBar = findViewById(R.id.airtemp_bar);


        // Set OnCheckedChangeListener for the water pump switch
        waterPumpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Water pump switch is turned on
                    Toast.makeText(Dashboard.this, "Water Pump is turned on", Toast.LENGTH_SHORT).show();
                } else {
                    // Water pump switch is turned off
                    Toast.makeText(Dashboard.this, "Water Pump is turned off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnCheckedChangeListener for the ventilation switch
        ventiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Ventilation switch is turned on
                    Toast.makeText(Dashboard.this, "Ventilation is turned on", Toast.LENGTH_SHORT).show();
                } else {
                    // Ventilation switch is turned off
                    Toast.makeText(Dashboard.this, "Ventilation is turned off", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean isPopupShowing = false;
    private PopupWindow popupWindow;

    public void ShowPopUp(View view) {
        if (!isPopupShowing) {
            // Inflate the custom popup window layout
            View popupView = LayoutInflater.from(this).inflate(R.layout.custom_menu_popup, null);

            // Create a PopupWindow object
            popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            // Find buttons inside the popup window
            LinearLayout txt0 = popupView.findViewById(R.id.profile_layout);
            LinearLayout txt1 = popupView.findViewById(R.id.dashboard_button);
            LinearLayout txt2 = popupView.findViewById(R.id.data_analytics_button);
            LinearLayout txt3 = popupView.findViewById(R.id.history_button);
            LinearLayout txt4 = popupView.findViewById(R.id.faqs_button);
            LinearLayout txt5 = popupView.findViewById(R.id.settings_button);
            LinearLayout txt6 = popupView.findViewById(R.id.logout_button);

            // Set click listeners for buttons
            txt0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button1 click
                    Toast.makeText(Dashboard.this, "Profile", Toast.LENGTH_SHORT).show();
                    // Dismiss the popup window
                    popupWindow.dismiss();
                }
            });

            txt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button1 click
                    Toast.makeText(Dashboard.this, "Dashboard", Toast.LENGTH_SHORT).show();
                    // Dismiss the popup window
                    popupWindow.dismiss();
                }
            });

            txt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button1 click
                    Toast.makeText(Dashboard.this, "Data Analytics", Toast.LENGTH_SHORT).show();
                    // Dismiss the popup window
                    popupWindow.dismiss();
                }
            });

            txt3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button1 click
                    Toast.makeText(Dashboard.this, "History", Toast.LENGTH_SHORT).show();
                    // Dismiss the popup window
                    popupWindow.dismiss();
                }
            });

            txt4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button1 click
                    Toast.makeText(Dashboard.this, "FAQs", Toast.LENGTH_SHORT).show();
                    // Dismiss the popup window
                    popupWindow.dismiss();
                }
            });

            txt5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button1 click
                    Toast.makeText(Dashboard.this, "Settings", Toast.LENGTH_SHORT).show();
                    // Dismiss the popup window
                    popupWindow.dismiss();
                }
            });

            txt6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button1 click
                    Toast.makeText(Dashboard.this, "Log-out", Toast.LENGTH_SHORT).show();
                    // Dismiss the popup window
                    popupWindow.dismiss();
                }
            });

            // Implement click listeners for other buttons similarly...

            // Show the popup window at a specific location on the screen
            // For example, show it below the toolbar navigation icon
            View toolbarNavigationIcon = findViewById(R.id.toolbar_navigation_icon);
            popupWindow.showAsDropDown(toolbarNavigationIcon);
            isPopupShowing = true;

            // Set a dismiss listener to detect when the popup is dismissed by clicking outside
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isPopupShowing = false;
                }
            });
        } else {
            // If popup is already showing, dismiss it
            popupWindow.dismiss();
            isPopupShowing = false;
        }
    }

}