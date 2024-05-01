package com.allstar.wirwo;

import android.content.Context;
import android.os.Handler;
import java.util.HashMap;
import java.util.Map;

public class AlertsDialogHelper implements OnDataChangeListener {
    private Context context;
    private Map<String, Long> lastAlertTimes = new HashMap<>();
    private static final long DEFAULT_COOLDOWN = 1 * 60 * 1000; // Default cooldown for alerts in milliseconds
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
        // Check if alerts are enabled
        if (!alertsValue) {
            // Alerts are turned off, do not show any alert
            return;
        }

        // Logic to check sensor readings and show alerts
        checkAndShowAlert(tempValue, maxSoilTempThresh, minSoilTempThresh, "Soil Temperature", "The Soil Temperature level is ", maxSoilTempThresh, minSoilTempThresh);
        checkAndShowAlert(moistureValue, maxSoilMoistureThresh, minSoilMoistureThresh, "Soil Moisture", "The Soil Moisture level is ", maxSoilMoistureThresh, minSoilMoistureThresh);
        checkAndShowAlert(humidity, maxHumidityThresh, minHumidityThresh, "Humidity", "The humidity level is ", maxHumidityThresh, minHumidityThresh);
        checkAndShowAlert(airtempValue, maxAirTempThresh, minAirTempThresh, "Air Temperature", "The Air Temperature level is ", maxAirTempThresh, minAirTempThresh);
    }

    private void checkAndShowAlert(double value, double maxValue, double minValue, String alertType, String messagePrefix, double maxThreshold, double minThreshold) {
        long currentTime = System.currentTimeMillis();
        long cooldown = calculateCooldown(maxThreshold, minThreshold);
        long lastAlertTime = lastAlertTimes.getOrDefault(alertType, 0L);

        if (value > maxValue || value < minValue) {
            if (currentTime - lastAlertTime > cooldown) {
                // Show alert
                String message = messagePrefix + (value > maxValue ? "above" : "below") + " the threshold.";
                showAlertDialog("High " + alertType + " Alert!", message, value > maxValue);
                // Update last alert time for this alert type
                lastAlertTimes.put(alertType, currentTime);
            }
        }
    }

    private long calculateCooldown(double maxValue, double minValue) {
        // Calculate cooldown based on thresholds
        // You can adjust this calculation as needed
        // For example, you can have different cooldowns for different threshold ranges
        return DEFAULT_COOLDOWN;
    }

    private void showAlertDialog(String title, String message, boolean isMax) {
        DialogHelper.showAlertDialog(context, title, message, isMax);
    }
}


