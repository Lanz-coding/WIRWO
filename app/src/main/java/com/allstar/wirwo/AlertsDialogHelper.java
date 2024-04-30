package com.allstar.wirwo;

import android.content.Context;
import android.os.Handler;

public class AlertsDialogHelper implements OnDataChangeListener {
    private Context context;
    private long lastAlertTime = 0;
    private static final long ALERT_COOLDOWN = 5 * 60 * 1000; // 5 minutes in milliseconds
    private Handler handler = new Handler();

    public AlertsDialogHelper(Context context) {
        this.context = context;
    }

    @Override
    public void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue,
                                 double minSoilTempThresh, double maxSoilTempThresh,
                                 double minSoilMoistureThresh, double maxSoilMoistureThresh,
                                 double minHumidityThresh, double maxHumidityThresh,
                                 double minAirTempThresh, double maxAirTempThresh) {
        // Your logic to check sensor readings and show alerts goes here
        // For example:
        if (humidity >= maxHumidityThresh) {
            // Check if enough time has passed since last alert
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAlertTime > ALERT_COOLDOWN) {
                // Show alert for high humidity
                showAlertDialog("High Humidity Alert", "The humidity level is above the threshold.", true);
                // Update last alert time
                lastAlertTime = currentTime;
            }
        } else if (humidity <= minHumidityThresh) {
            // Check if enough time has passed since last alert
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAlertTime > ALERT_COOLDOWN) {
                // Show alert for low humidity
                showAlertDialog("Low Humidity Alert", "The humidity level is below the threshold.", false);
                // Update last alert time
                lastAlertTime = currentTime;
            }
        }

        // Similar logic for other sensor readings...
    }

    private void showAlertDialog(String title, String message, boolean isMax) {
        DialogHelper.showAlertDialog(context, title, message, isMax);
    }
}
