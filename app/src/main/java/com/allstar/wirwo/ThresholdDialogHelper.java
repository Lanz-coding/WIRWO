package com.allstar.wirwo;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

public class ThresholdDialogHelper {

    public interface ThresholdDialogCallback {
        void onThresholdSelected(int thresholdValue);
    }

    public static void showSoilTempThresholdDialog(Context context, ThresholdDialogCallback callback) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_threshold_layout);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView okButton = dialog.findViewById(R.id.ok_button);
        TextView cancelButton = dialog.findViewById(R.id.cancel_button);
        Slider slider = dialog.findViewById(R.id.slider);
        TextView progressTextView = dialog.findViewById(R.id.slider_progress);

        dialogTitle.setText("Set Soil Temperature Threshold");

        int current = DatabaseHelper.getSoilTempThreshold();
        progressTextView.setText(current + "°C");
        slider.setValue(current);
        slider.setValueTo(50);

        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (callback != null) {
                    // Pass the selected threshold value to the callback
                    callback.onThresholdSelected((int) slider.getValue());
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when Cancel button is clicked
                dialog.dismiss();
            }
        });

        // Set progress change listener for the slider
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {

                progressTextView.setText(String.valueOf((int)value) + "°C");
            }
        });

        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                // Returning an empty string will hide the progress tooltip
                return "";
            }
        });

        // Show the dialog
        dialog.show();
    }

    public static void showSoilMoistureThresholdDialog(Context context, ThresholdDialogCallback callback) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_threshold_layout);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView okButton = dialog.findViewById(R.id.ok_button);
        TextView cancelButton = dialog.findViewById(R.id.cancel_button);
        Slider slider = dialog.findViewById(R.id.slider);
        TextView progressTextView = dialog.findViewById(R.id.slider_progress);

        dialogTitle.setText("Set Soil Moisture Threshold");

        int current = DatabaseHelper.getSoilMoistureThreshold();
        progressTextView.setText(current + "%");
        slider.setValue(current);
        slider.setValueTo(100);

        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (callback != null) {
                    // Pass the selected threshold value to the callback
                    callback.onThresholdSelected((int) slider.getValue());
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when Cancel button is clicked
                dialog.dismiss();
            }
        });

        // Set progress change listener for the slider
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Update progress TextView when slider value changes
                progressTextView.setText(String.valueOf((int)value) + "%");
            }
        });

        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                // Returning an empty string will hide the progress tooltip
                return "";
            }
        });

        // Show the dialog
        dialog.show();
    }


    public static void showHumidityThresholdDialog(Context context, ThresholdDialogCallback callback) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_threshold_layout);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView okButton = dialog.findViewById(R.id.ok_button);
        TextView cancelButton = dialog.findViewById(R.id.cancel_button);
        Slider slider = dialog.findViewById(R.id.slider);
        TextView progressTextView = dialog.findViewById(R.id.slider_progress);

        dialogTitle.setText("Set Soil Temperature Threshold");

        int current = DatabaseHelper.getHumidityThreshold();
        progressTextView.setText(current + "%");
        slider.setValue(current);
        slider.setValueTo(100);

        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (callback != null) {
                    // Pass the selected threshold value to the callback
                    callback.onThresholdSelected((int) slider.getValue());
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when Cancel button is clicked
                dialog.dismiss();
            }
        });

        // Set progress change listener for the slider
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Update progress TextView when slider value changes
                progressTextView.setText(String.valueOf((int)value) + "%");
            }
        });

        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                // Returning an empty string will hide the progress tooltip
                return "";
            }
        });

        // Show the dialog
        dialog.show();
    }

    public static void showAirTempThresholdDialog(Context context, ThresholdDialogCallback callback) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_threshold_layout);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView okButton = dialog.findViewById(R.id.ok_button);
        TextView cancelButton = dialog.findViewById(R.id.cancel_button);
        Slider slider = dialog.findViewById(R.id.slider);
        TextView progressTextView = dialog.findViewById(R.id.slider_progress);

        dialogTitle.setText("Set Air Temperature Threshold");

        int current = DatabaseHelper.getAirTempThreshold();
        progressTextView.setText(current + "°C");
        slider.setValue(current);
        slider.setValueTo(50);

        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (callback != null) {
                    // Pass the selected threshold value to the callback
                    callback.onThresholdSelected((int) slider.getValue());
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when Cancel button is clicked
                dialog.dismiss();
            }
        });

        // Set progress change listener for the slider
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Update progress TextView when slider value changes
                progressTextView.setText(String.valueOf((int)value) + "°C");
            }
        });

        slider.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                // Returning an empty string will hide the progress tooltip
                return "";
            }
        });

        // Show the dialog
        dialog.show();
    }
}
