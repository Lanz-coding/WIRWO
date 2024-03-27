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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PopupWindowHelper {

    private PopupWindow popupWindow;
    private boolean isPopupShowing = false;
    private Context context;

    private FirebaseAuth auth;

    public PopupWindowHelper(Context context) {
        this.context = context;
        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();
    }

    public void showPopup(View anchorView) {
        if (!isPopupShowing) {
            // Inflate the custom popup window layout
            View popupView = LayoutInflater.from(context).inflate(R.layout.custom_menu_popup, null);

            // Get reference to TextView
            TextView usernameText = popupView.findViewById(R.id.view_profile_text);

            // Initialize FirebaseAuth instance
            auth = FirebaseAuth.getInstance();

            FirebaseUser currentUser = auth.getCurrentUser();

            // Check if user is not null
            if (currentUser != null) {
                // Extract the email address
                String email = currentUser.getEmail();

                // If email is not null, extract the username (prefix before "@")
                if (email != null) {
                    int index = email.indexOf('@');
                    if (index != -1) {
                        String username = email.substring(0, index);
                        usernameText.setText(username);
                    } else {
                        // Handle case where email doesn't contain "@" symbol
                        usernameText.setText("User"); // Or set a default message
                    }
                } else {
                    // Handle case where currentUser.getEmail() is null
                    usernameText.setText("User"); // Or set a default message
                }
            } else {
                // Set default text if user is null
                String text = "User";
                usernameText.setText(text);
            }


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
