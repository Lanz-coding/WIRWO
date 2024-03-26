package com.example.wirwo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;


public class faqs extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faqs);



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
            txt0.setOnClickListener(v -> {
                // Handle button1 click
                Toast.makeText(faqs.this, "Profile", Toast.LENGTH_SHORT).show();
                // Dismiss the popup window
                popupWindow.dismiss();
            });

            txt1.setOnClickListener(v -> {
                try {
                    // Handle button1 click
                    Intent intent = new Intent(faqs.this, Dashboard.class);
                    startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(faqs.this, "Error starting Dashboard's activity", Toast.LENGTH_SHORT).show();
                }

                // Dismiss the popup window
                popupWindow.dismiss();
            });

            txt2.setOnClickListener(v -> {
                // Handle button1 click
                Toast.makeText(faqs.this, "Data Analytics", Toast.LENGTH_SHORT).show();
                // Dismiss the popup window
                popupWindow.dismiss();
            });

            txt3.setOnClickListener(v -> {
                // Handle button1 click
                Toast.makeText(faqs.this, "History", Toast.LENGTH_SHORT).show();
                // Dismiss the popup window
                popupWindow.dismiss();
            });

            txt4.setOnClickListener(v -> {
                // Handle button1 click
                Toast.makeText(faqs.this, "FAQs", Toast.LENGTH_SHORT).show();
                // Dismiss the popup window
                popupWindow.dismiss();
            });

            txt5.setOnClickListener(v -> {
                // Handle button1 click
                Toast.makeText(faqs.this, "Settings", Toast.LENGTH_SHORT).show();
                // Dismiss the popup window
                popupWindow.dismiss();
            });

            txt6.setOnClickListener(v -> {
                // Handle button1 click
                // Dismiss the popup window
                popupWindow.dismiss();
            });

            // Implement click listeners for other buttons similarly...

            // Show the popup window at a specific location on the screen
            // For example, show it below the toolbar navigation icon
            View toolbarNavigationIcon = findViewById(R.id.toolbar_navigation_icon);
            popupWindow.showAsDropDown(toolbarNavigationIcon);
            isPopupShowing = true;

            // Set a dismiss listener to detect when the popup is dismissed by clicking outside
            popupWindow.setOnDismissListener(() -> isPopupShowing = false);
        } else {
            // If popup is already showing, dismiss it
            popupWindow.dismiss();
            isPopupShowing = false;
        }


    }
    }
