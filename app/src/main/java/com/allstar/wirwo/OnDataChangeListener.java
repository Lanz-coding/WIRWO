package com.allstar.wirwo;

public interface OnDataChangeListener {
    void onDatabaseChange(double humidityValue, boolean ventiValue, boolean value, double moistureValue, double tempValue, double airtempValue,
                          boolean alertsValue, boolean notifsValue,
                          double minSoilTempThreshold, double maxSoilTempThreshold,
                          double minSoilMoistureThreshold, double maxSoilMoistureThreshold,
                          double minHumidityThreshold, double maxHumidityThreshold,
                          double minAirTempThreshold, double maxAirTempThreshold);
}


