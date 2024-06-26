package com.allstar.wirwo;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PopupWindowHelper {

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


    public void showPopup(View anchorView, int xOffset, int yOffset) {
        // Create a Dialog object
        Dialog dialog = new Dialog(context);

        // Inflate the custom popup window layout
        View popupView = LayoutInflater.from(context).inflate(R.layout.custom_menu_popup, null);

        // Set the layout for the dialog
        dialog.setContentView(popupView);

        // Get reference to TextView
        TextView usernameText = popupView.findViewById(R.id.view_profile_text);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        // Check if user is not null
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("username");

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.getValue(String.class);
                        usernameText.setText(username);
                    } else {
                        // Handle case where username data doesn't exist
                        usernameText.setText("User"); // Or set a default message
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    String text = "User";
                    usernameText.setText(text);
                }
            });
        } else {
            // Set default text if user is null
            String text = "User";
            usernameText.setText(text);
        }

        // Find buttons inside the popup window
        LinearLayout profile = popupView.findViewById(R.id.profile_layout);
        LinearLayout dashboard = popupView.findViewById(R.id.dashboard_button);
        LinearLayout data_analysis = popupView.findViewById(R.id.data_analytics_button);
        LinearLayout history = popupView.findViewById(R.id.history_button);
        LinearLayout faqs = popupView.findViewById(R.id.faqs_button);
        LinearLayout settings = popupView.findViewById(R.id.settings_button);
        LinearLayout logout = popupView.findViewById(R.id.logout_button);

        // Update 'activeItem' based on the current activity (replace with your logic)
        String currentActivity = ((Activity) context).getClass().getSimpleName();
        switch (currentActivity) {
            case "DashboardActivity":
                activeItem = dashboard;
                break;
            case "FAQsActivity":
                activeItem = faqs;
                break;
            case "SettingsActivity":
                activeItem = settings;
                break;
            case "DataAnalyticsActivity":
                activeItem = data_analysis;
                break;

            case "HistoryActivity":
                activeItem = history;
        }

        // Set a darker background color for the active item
        if (activeItem != null) {
            int color = Color.parseColor("#102820");  // Parse hex code to integer color value
            activeItem.setBackgroundColor(color);
        }

        // Implement click listeners for other buttons similarly...
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
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        dashboard.setOnClickListener(v -> {
            if (!currentActivity.equals("DashboardActivity")) { // Check if not on the current page
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, DashboardActivity.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting Dashboard's activity", Toast.LENGTH_SHORT).show();
                }

                // Dismiss the popup window
                dialog.dismiss();
            }
        });

        faqs.setOnClickListener(v -> {
            if (!currentActivity.equals("FAQsActivity")) { // Check if not on the current page
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, FAQsActivity.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting FAQs's activity", Toast.LENGTH_SHORT).show();
                }

                // Dismiss the popup window
                dialog.dismiss();
            }
        });

        data_analysis.setOnClickListener(v -> {
            if (!currentActivity.equals("Data_Analysis")) { // Check if not on the current page
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, DataAnalyticsActivity.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting Data_Analysis activity", Toast.LENGTH_SHORT).show();
                }

                // Dismiss the popup window
                dialog.dismiss();
            }
        });

        history.setOnClickListener(v -> {
            if (!currentActivity.equals("HistoryActivity")) { // Check if not on the current page
                try {
                    // Handle button1 click
                    Intent intent = new Intent(context, HistoryActivity.class);
                    context.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    // Handle the exception appropriately, e.g., show an error message
                    Toast.makeText(context, "Error starting History's activity", Toast.LENGTH_SHORT).show();
                }
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        logout.setOnClickListener(v -> {
            // Display confirmation dialog
            DialogHelper.showDialogWithOkCancel(context,
                    "Log Out",
                    "Are you sure you want to log-out?",
                    v1 -> {
                        // OK button clicked
                        auth.signOut();

                        // Display success message with your app icon
                        showToastWithAppIcon("Logged Out Successfully", true);

                        // Optionally, redirect user to login activity
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);

                        // Close the popup window
                        dialog.dismiss();
                    },
                    v12 -> {
                        // Cancel button clicked
                        // Dismiss the dialog (nothing to do here)
                    });
        });

        // Set the width and height of the dialog
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = xOffset; // Set x offset
        layoutParams.y = yOffset; // Set y offset

        // Show the dialog
        dialog.show();

        // Set a dismiss listener to detect when the dialog is dismissed
        dialog.setOnDismissListener(dialogInterface -> {
            // Reset the flag
            isPopupShowing = false;
        });

        // Set the flag to indicate that the dialog is showing
        isPopupShowing = true;
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
