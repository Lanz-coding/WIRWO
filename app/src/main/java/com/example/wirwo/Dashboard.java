package com.example.wirwo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.example.wirwo.LoginActivity;

public class Dashboard extends Activity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String POPUP_FLAG = "popupShown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);

        ImageView toolbarNavigationIcon = findViewById(R.id.toolbar_navigation_icon);
        toolbarNavigationIcon.setOnClickListener(v -> ShowPopUp(toolbarNavigationIcon));

        // Check if the popup has been shown before
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean popupShown = settings.getBoolean(POPUP_FLAG, false);

        if (!popupShown) {
            // Show the popup if it hasn't been shown before
            ShowPopUp(toolbarNavigationIcon);
            // Set the flag to true to indicate that the popup has been shown
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(POPUP_FLAG, true);
            editor.apply();
        }

        // Retrieve views using their IDs
        Switch waterPumpSwitch = findViewById(R.id.waterPumpSwitch);
        Switch ventiSwitch = findViewById(R.id.ventiSwitch);

        // Set OnCheckedChangeListener for the water pump switch
        waterPumpSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Water pump switch is turned on
                Toast.makeText(this, "Water Pump is turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Water pump switch is turned off
                Toast.makeText(this, "Water Pump is turned off", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnCheckedChangeListener for the ventilation switch
        ventiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Ventilation switch is turned on
                Toast.makeText(this, "Ventilation is turned on", Toast.LENGTH_SHORT).show();
            } else {
                // Ventilation switch is turned off
                Toast.makeText(this, "Ventilation is turned off", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ShowPopUp(View anchorView) {
        // Inflate the popup menu layout
        View popupView = LayoutInflater.from(this).inflate(R.layout.custom_menu_popup, null);

        // Create a PopupWindow with the inflated layout
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // Allows touches outside of the PopupWindow to dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Show the popup menu anchored to the toolbar navigation icon
        popupWindow.showAsDropDown(anchorView);

        // Handle clicks on popup menu items
        handlePopupMenuClicks(popupView);
    }

    private void handlePopupMenuClicks(View popupView) {
        // Find the views within the popup menu and set click listeners
        LinearLayout profileLayout = popupView.findViewById(R.id.profile_layout);
        LinearLayout dashboardButton = popupView.findViewById(R.id.dashboard_button);
        LinearLayout dataAnalyticsButton = popupView.findViewById(R.id.data_analytics_button);
        LinearLayout historyButton = popupView.findViewById(R.id.history_button);
        LinearLayout faqsButton = popupView.findViewById(R.id.faqs_button);
        LinearLayout settingsButton = popupView.findViewById(R.id.settings_button);
        LinearLayout logoutButton = popupView.findViewById(R.id.logout_button);

        // Set click listeners for each menu item
        profileLayout.setOnClickListener(v -> {
            // Handle profile layout click event
            Toast.makeText(Dashboard.this, "Profile clicked", Toast.LENGTH_SHORT).show();
        });

        dashboardButton.setOnClickListener(v -> {
            // Handle dashboard button click event
            Toast.makeText(Dashboard.this, "Dashboard clicked", Toast.LENGTH_SHORT).show();
        });

        dataAnalyticsButton.setOnClickListener(v -> {
            // Handle data analytics button click event
            Toast.makeText(Dashboard.this, "Data Analytics clicked", Toast.LENGTH_SHORT).show();
        });

        historyButton.setOnClickListener(v -> {
            // Handle history button click event
            Toast.makeText(Dashboard.this, "History clicked", Toast.LENGTH_SHORT).show();
        });

        faqsButton.setOnClickListener(v -> {
            // Handle FAQs button click event
            Toast.makeText(Dashboard.this, "FAQs clicked", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            // Handle settings button click event
            Toast.makeText(Dashboard.this, "Settings clicked", Toast.LENGTH_SHORT).show();// You can add your code here to handle the settings button click event
        });

        logoutButton.setOnClickListener(v -> {
            // Handle logout button click event
            AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Redirect to the login activity
                Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back to it on back press
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                // Dismiss the dialog if "No" is clicked
                dialog.dismiss();
            });
            builder.show();
        });
    }
}