package com.example.wirwo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingDialogHelper {

    private Dialog dialog;
    private Context context;

    public LoadingDialogHelper(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Call method to set tint color for ProgressBar
        setProgressBarColor();
    }

    // Method to set tint color for ProgressBar
    private void setProgressBarColor() {
        // Find the ProgressBar in the layout
        ProgressBar progressBar = dialog.findViewById(R.id.loading_progress_bar);
        // Set the tint color programmatically
        progressBar.getIndeterminateDrawable().setColorFilter(
                context.getResources().getColor(R.color.white),
                PorterDuff.Mode.SRC_IN
        );
    }

    public void showDialog(String message) {
        TextView titleTextView = dialog.findViewById(R.id.dialog_title);
        if (titleTextView != null) {
            titleTextView.setText(message);
        }
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
