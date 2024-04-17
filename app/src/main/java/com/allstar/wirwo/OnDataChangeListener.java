package com.allstar.wirwo;

public interface OnDataChangeListener {
    void onDatabaseChange(double humidity, boolean ledValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue);
}


