package com.allstar.wirwo;

import android.app.Dialog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogHelper {


    public interface OnOkClickListener {
        void onOkClicked();
    }

    public static void showDialogWithTitle(Context context, String title, String message, final OnOkClickListener okClickListener) {
        // Create and instantiate dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);

        // Set dialog window attributes
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        TextView dialogButton = dialog.findViewById(R.id.ok_button);

        // Set title and message
        dialogTitle.setText(title);
        dialogMessage.setText(message);

        // Set button click listener
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when button is clicked
                dialog.dismiss();
                if (okClickListener != null) {
                    okClickListener.onOkClicked(); // Invoke onOkClicked method
                }
            }
        });

        // Show the dialog
        dialog.show();
    }

    public static void showAlertDialog(final Context context, String title, String message, boolean isMax) {
        // Create and instantiate dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);

        // Set dialog window attributes
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        TextView dialogButton = dialog.findViewById(R.id.ok_button);

        dialogButton.setText("GO TO DASHBOARD");

        if (isMax) {
            // Set title and message
            dialogTitle.setText(title);
            dialogMessage.setText(message);
        } else {
            // Set title and message
            dialogTitle.setText(title);
            dialogMessage.setText(message);
        }

        // Set button click listener
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when button is clicked
                dialog.dismiss();
                // Check if the current activity is not DashboardActivity before navigating
                if (!(context instanceof DashboardActivity)) {
                    Intent intent = new Intent(context, DashboardActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        // Show the dialog
        dialog.show();
    }


    public static void showSuccessChangePassDialog(final Activity activity, final OnOkClickListener okClickListener) {
        // Create and instantiate dialog
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_layout);

        // Set dialog window attributes
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        TextView dialogButton = dialog.findViewById(R.id.ok_button);

        // Set title and message
        dialogTitle.setText("Success");
        dialogMessage.setText("Password Successfully Changed. You need to log-in again.");
        dialogButton.setText("OK");

        // Set button click listener
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okClickListener != null) {
                    okClickListener.onOkClicked(); // Invoke onOkClicked method if listener is not null
                }
                dialog.dismiss(); // Dismiss the dialog after the button is clicked
            }
        });

        // Show the dialog
        dialog.show();
    }



    public static void showNoIntenetDialog(final Activity activity) {
        // Create and instantiate dialog
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_layout);

        // Set dialog window attributes
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        TextView dialogButton = dialog.findViewById(R.id.ok_button);

        // Set title and message
        dialogTitle.setText("You're Offline");
        dialogMessage.setText("Please check your internet connection and launch the app again.");
        dialogButton.setText("EXIT");

        // Set button click listener
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when button is clicked
                dialog.dismiss();
                // Finish activity and all activities below it in the stack
                activity.finishAffinity();
            }
        });

        // Show the dialog
        dialog.show();
    }

    public static void showDialogWithOkCancel(Context context, String title, String message, View.OnClickListener okClickListener, View.OnClickListener cancelClickListener) {
        // Create and instantiate dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout_ok_cancel);

        // Set dialog window attributes
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_message);
        TextView okButton = dialog.findViewById(R.id.ok_button);
        TextView cancelButton = dialog.findViewById(R.id.cancel_button);

        // Set message
        dialogTitle.setText(title);
        dialogMessage.setText(message);

        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (okClickListener != null) {
                    okClickListener.onClick(v);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when Cancel button is clicked
                dialog.dismiss();
                if (cancelClickListener != null) {
                    cancelClickListener.onClick(v);
                }
            }
        });

        // Show the dialog
        dialog.show();
    }
}
