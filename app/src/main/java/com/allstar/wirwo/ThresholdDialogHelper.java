package com.allstar.wirwo;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.slider.RangeSlider;

public class ThresholdDialogHelper {

    public interface ThresholdDialogCallback {
        void onThresholdSelected(int minThresholdValue, int maxThresholdValue);
    }

    public static void showSoilTempThresholdDialog(Context context, ThresholdDialogCallback callback) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_threshold_layout);

        // Initialize views from dialog layout
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView okButton = dialog.findViewById(R.id.ok_button);
        TextView cancelButton = dialog.findViewById(R.id.cancel_button);
        RangeSlider rangeSlider = dialog.findViewById(R.id.range_slider);
        TextView progressTextView = dialog.findViewById(R.id.slider_progress);

        dialogTitle.setText("Set Soil Temperature Threshold");

        int currentMin = (int) DatabaseHelper.getMinSoilTempThreshold();
        int currentMax = (int) DatabaseHelper.getMaxSoilTempThreshold();
        progressTextView.setText(currentMin + "°C - " + currentMax + "°C");
        rangeSlider.setValues((float) currentMin, (float) currentMax);
        rangeSlider.setValueFrom(0);
        rangeSlider.setValueTo(50);

        // Set button click listeners
        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (callback != null) {
                    // Convert Float values to int
                    int minValue = Math.round(rangeSlider.getValues().get(0));
                    int maxValue = Math.round(rangeSlider.getValues().get(1));
                    // Pass the selected threshold values to the callback
                    callback.onThresholdSelected(minValue, maxValue);
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

        // Set change listener for the range slider
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                // Update progress TextView when slider values change
                int minValue = Math.round(slider.getValues().get(0));
                int maxValue = Math.round(slider.getValues().get(1));
                progressTextView.setText(String.valueOf(minValue) + "°C - " + String.valueOf(maxValue) + "°C");
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
        RangeSlider rangeSlider = dialog.findViewById(R.id.range_slider);
        TextView progressTextView = dialog.findViewById(R.id.slider_progress);

        dialogTitle.setText("Set Soil Moisture Threshold");

        int currentMin = (int) DatabaseHelper.getMinSoilMoistureThreshold();
        int currentMax = (int) DatabaseHelper.getMaxSoilMoistureThreshold();
        progressTextView.setText(currentMin + "% - " + currentMax + "%");
        rangeSlider.setValues((float) currentMin, (float) currentMax);
        rangeSlider.setValueFrom(0);
        rangeSlider.setValueTo(50);

        // Set button click listeners
        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (callback != null) {
                    // Convert Float values to int
                    int minValue = Math.round(rangeSlider.getValues().get(0));
                    int maxValue = Math.round(rangeSlider.getValues().get(1));
                    // Pass the selected threshold values to the callback
                    callback.onThresholdSelected(minValue, maxValue);
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

        // Set change listener for the range slider
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                // Update progress TextView when slider values change
                int minValue = Math.round(slider.getValues().get(0));
                int maxValue = Math.round(slider.getValues().get(1));
                progressTextView.setText(String.valueOf(minValue) + "% - " + String.valueOf(maxValue) + "%");
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
        RangeSlider rangeSlider = dialog.findViewById(R.id.range_slider);
        TextView progressTextView = dialog.findViewById(R.id.slider_progress);

        dialogTitle.setText("Set Humidity Threshold");

        int currentMin = (int) DatabaseHelper.getMinHumidityThreshold();
        int currentMax = (int) DatabaseHelper.getMaxHumidityThreshold();
        progressTextView.setText(currentMin + "% - " + currentMax + "%");
        rangeSlider.setValues((float) currentMin, (float) currentMax);
        rangeSlider.setValueFrom(0);
        rangeSlider.setValueTo(50);

        // Set button click listeners
        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (callback != null) {
                    // Convert Float values to int
                    int minValue = Math.round(rangeSlider.getValues().get(0));
                    int maxValue = Math.round(rangeSlider.getValues().get(1));
                    // Pass the selected threshold values to the callback
                    callback.onThresholdSelected(minValue, maxValue);
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

        // Set change listener for the range slider
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                // Update progress TextView when slider values change
                int minValue = Math.round(slider.getValues().get(0));
                int maxValue = Math.round(slider.getValues().get(1));
                progressTextView.setText(String.valueOf(minValue) + "% - " + String.valueOf(maxValue) + "%");
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
        RangeSlider rangeSlider = dialog.findViewById(R.id.range_slider);
        TextView progressTextView = dialog.findViewById(R.id.slider_progress);

        dialogTitle.setText("Set AIr Temperature Threshold");

        int currentMin = (int) DatabaseHelper.getMinAirTempThreshold();
        int currentMax = (int) DatabaseHelper.getMaxAirTempThreshold();
        progressTextView.setText(currentMin + "°C - " + currentMax + "°C");
        rangeSlider.setValues((float) currentMin, (float) currentMax);
        rangeSlider.setValueFrom(0);
        rangeSlider.setValueTo(50);

        // Set button click listeners
        // Set button click listeners
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss dialog when OK button is clicked
                dialog.dismiss();
                if (callback != null) {
                    // Convert Float values to int
                    int minValue = Math.round(rangeSlider.getValues().get(0));
                    int maxValue = Math.round(rangeSlider.getValues().get(1));
                    // Pass the selected threshold values to the callback
                    callback.onThresholdSelected(minValue, maxValue);
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

        // Set change listener for the range slider
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                // Update progress TextView when slider values change
                int minValue = Math.round(slider.getValues().get(0));
                int maxValue = Math.round(slider.getValues().get(1));
                progressTextView.setText(String.valueOf(minValue) + "°C - " + String.valueOf(maxValue) + "°C");
            }
        });


        // Show the dialog
        dialog.show();
    }
}
