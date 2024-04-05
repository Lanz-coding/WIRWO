package com.example.wirwo;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DialogHelper {

    public static void showDialog(Context context, String title, String message) {
        showDialogWithTitle(context, title, message);
    }

    public static void showDialogWithTitle(Context context, String title, String message) {
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
        TextView okButton = dialog.findViewById(R.id.ok_button);

        // Set title and message
        dialogTitle.setText(title);
        dialogMessage.setText(message);

        // Set button click listener
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when button is clicked
                dialog.dismiss();
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
