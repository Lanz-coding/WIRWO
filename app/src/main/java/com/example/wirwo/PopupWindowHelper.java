package com.example.wirwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PopupWindowHelper {

    private PopupWindow popupWindow;
    private boolean isPopupShowing = false;
    private Context context;

    private FirebaseAuth auth;

    // Reference to the currently active LinearLayout in the popup
    private LinearLayout activeItem;

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
            LinearLayout data_analysis = popupView.findViewById(R.id.data_analytics_button);
            LinearLayout history = popupView.findViewById(R.id.history_button);
            LinearLayout faqs = popupView.findViewById(R.id.faqs_button);
            LinearLayout settings = popupView.findViewById(R.id.settings_button);
            LinearLayout logout = popupView.findViewById(R.id.logout_button);


            // Update 'activeItem' based on the current activity (replace with your logic)
            String currentActivity = ((Activity) context).getLocalClassName();
            switch (currentActivity) {
                case "Dashboard":
                    activeItem = dashboard;
                    break;
                case "faqs":
                    activeItem = faqs;
                    break;
                case "SettingsActivity":
                    activeItem = settings;
                    break;
                case "Data_Analysis":
                    activeItem = data_analysis;

                    break;
            }

            // Set click listeners for buttons
            profile.setOnClickListener(v -> {
                // Handle button1 click
                Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show();
                // Dismiss the popup window
                popupWindow.dismiss();
            });

            analytics.setOnClickListener(v -> {
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, data_analysis.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting Data Analysis", Toast.LENGTH_SHORT).show();
                }
            });

            settings.setOnClickListener(v -> {
                if (!currentActivity.equals("SettingsActivity")) { // Check if not on the current page
                    try {
                        // Handle button1 click
                        Intent intent = new Intent(context, SettingsActivity.class);
                        context.startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        // Handle the exception appropriately, e.g., show an error message
                        Toast.makeText(context, "Error starting Settings's activity", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dashboard.setOnClickListener(v -> {
                if (!currentActivity.equals("Dashboard")) { // Check if not on the current page
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
                }
            });

            faqs.setOnClickListener(v -> {
                if (!currentActivity.equals("faqs")) { // Check if not on the current page
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
                }
            });

            data_analysis.setOnClickListener(v -> {
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, Data_Analysis.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting Data_Analysis activity", Toast.LENGTH_SHORT).show();
                }

                // Dismiss the popup window
                popupWindow.dismiss();
            });



            logout.setOnClickListener(v -> {
                // Display confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User confirmed, proceed with logout
                                // Sign out the user from Firebase
                                auth.signOut();

                                // Display success message with your app icon
                                showToastWithAppIcon("Logged Out Successfully", true);

                                // Close the popup window
                                popupWindow.dismiss();

                                // Optionally, redirect user to login activity
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User canceled, do nothing or dismiss the dialog
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            });

            // Set a darker background color for the active item
            if (activeItem != null) {
                int color = Color.parseColor("#102820");  // Parse hex code to integer color value
                activeItem.setBackgroundColor(color);
            }


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

    // Method to show custom toast with app icon
    private void showToastWithAppIcon(String message, boolean isSuccess) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_layout, null);

        ImageView iconImageView = layout.findViewById(R.id.toast_icon);
        TextView messageTextView = layout.findViewById(R.id.toast_text);

        // Set the app icon based on success or failure
        if (isSuccess) {
            // Set your success icon
            iconImageView.setImageResource(R.drawable.white_wirwo); // Replace with your success icon
        } else {
            // Set your failure icon
            iconImageView.setImageResource(R.drawable.white_wirwo); // Replace with your failure icon
        }

        // Set the message
        messageTextView.setText(message);

        // Create and show the toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
