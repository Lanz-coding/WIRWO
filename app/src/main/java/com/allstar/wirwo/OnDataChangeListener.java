package com.allstar.wirwo;

public interface OnDataChangeListener {
    void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue,
                          double airtempValue, boolean alertsValue, boolean notifsValue,
                          double soilTempThresh, double soilMoistureThresh, double humidityThresh, double airTempThresh);
}


