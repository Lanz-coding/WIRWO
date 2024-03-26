package com.example.wirwo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class PopupWindowHelper {

    private PopupWindow popupWindow;
    private boolean isPopupShowing = false;
    private Context context;

    public PopupWindowHelper(Context context) {
        this.context = context;
    }

    public void showPopup(View anchorView) {
        if (!isPopupShowing) {
            // Inflate the custom popup window layout
            View popupView = LayoutInflater.from(context).inflate(R.layout.custom_menu_popup, null);

            // Create a PopupWindow object
            popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            // Find buttons inside the popup window
            LinearLayout profile = popupView.findViewById(R.id.profile_layout);
            LinearLayout dashboard = popupView.findViewById(R.id.dashboard_button);
            LinearLayout analytics = popupView.findViewById(R.id.data_analytics_button);
            LinearLayout history = popupView.findViewById(R.id.history_button);
            LinearLayout faqs = popupView.findViewById(R.id.faqs_button);
            LinearLayout settings = popupView.findViewById(R.id.settings_button);
            LinearLayout logout = popupView.findViewById(R.id.logout_button);

            // Set click listeners for buttons
            profile.setOnClickListener(v -> {
                // Handle button1 click
                Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show();
                // Dismiss the popup window
                popupWindow.dismiss();
            });


            settings.setOnClickListener(v -> {
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, SettingsActivity.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting Settings's activity", Toast.LENGTH_SHORT).show();
                }

            });

            dashboard.setOnClickListener(v -> {
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, Dashboard.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting Dashboard's activity", Toast.LENGTH_SHORT).show();
                }

                // Dismiss the popup window
                popupWindow.dismiss();
            });

            faqs.setOnClickListener(v -> {
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, faqs.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting FAQs's activity", Toast.LENGTH_SHORT).show();
                }

                // Dismiss the popup window
                popupWindow.dismiss();
            });

            // Implement click listeners for other buttons similarly...

            // Show the popup window at a specific location on the screen
            // For example, show it below the toolbar navigation icon
            popupWindow.showAsDropDown(anchorView);
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
