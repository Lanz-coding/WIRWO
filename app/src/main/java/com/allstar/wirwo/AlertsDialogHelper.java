package com.allstar.wirwo;

import android.content.Context;
import android.os.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsDialogHelper implements OnDataChangeListener {
    private Context context;
    private Map<String, Map<String, Long>> lastAlertTimes = new HashMap<>();
    private static final long DEFAULT_COOLDOWN = 1 * 60 * 1000; // Default cooldown for alerts in milliseconds
    private Handler handler = new Handler();

    public AlertsDialogHelper(Context context) {
        this.context = context;
    }

    @Override
    public void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue,
                                 double moistureValue, double tempValue, double airtempValue,
                                 boolean alertsValue, boolean notifsValue,
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
        checkAndShowAlert(tempValue, maxSoilTempThresh, minSoilTempThresh, "Soil Temperature", "The Soil Temperature level is ");
        checkAndShowAlert(moistureValue, maxSoilMoistureThresh, minSoilMoistureThresh, "Soil Moisture", "The Soil Moisture level is ");
        checkAndShowAlert(humidity, maxHumidityThresh, minHumidityThresh, "Humidity", "The humidity level is ");
        checkAndShowAlert(airtempValue, maxAirTempThresh, minAirTempThresh, "Air Temperature", "The Air Temperature level is ");
    }

    private void checkAndShowAlert(double value, double maxValue, double minValue, String alertType, String messagePrefix) {
        long currentTime = System.currentTimeMillis();
        Map<String, Long> alertTypeMap = lastAlertTimes.getOrDefault(alertType, new HashMap<>());
        long lastMaxAlertTime = alertTypeMap.getOrDefault("max", 0L);
        long lastMinAlertTime = alertTypeMap.getOrDefault("min", 0L);

        if (value > maxValue && currentTime - lastMaxAlertTime > DEFAULT_COOLDOWN) {
            // Show alert for exceeding maximum threshold
            String message = messagePrefix + "above the maximum threshold.";
            showAlertDialog("High " + alertType + " Alert!", message);
            alertTypeMap.put("max", currentTime);
        }

        if (value < minValue && currentTime - lastMinAlertTime > DEFAULT_COOLDOWN) {
            // Show alert for exceeding minimum threshold
            String message = messagePrefix + "below the minimum threshold.";
            showAlertDialog("Low " + alertType + " Alert!", message);
            alertTypeMap.put("min", currentTime);
        }

        lastAlertTimes.put(alertType, alertTypeMap);
    }

    private void showAlertDialog(String title, String message) {
        DialogHelper.showAlertDialog(context, title, message);
    }
}