package com.allstar.wirwo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.juanarton.arcprogressbar.ArcProgressBar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

public class DataAnalyticsActivity extends Activity implements OnDataChangeListener {
    private AlertsDialogHelper alertsDialogHelper;
    private DatabaseHelper helper;
    private LineChart airLineChart;
    private LineChart soilLineChart;
    private ProgressBar soilTempProgressBar;
    private ProgressBar airTempProgressBar;
    private ArcProgressBar humidityProgressBar;
    private ArcProgressBar soilMoistureProgressBar;
    private TextView soilTempTextView;
    private TextView airTempTextView;
    private TextView humidityValue;
    private TextView soilMoistureValue;
    private TextView timeText;
    private Handler handler = new Handler();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HH:mm:ss", Locale.getDefault());

    // Firestore
    private FirebaseFirestore db;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analytics);

        // Initialize the AlertsDialogHelper
        alertsDialogHelper = new AlertsDialogHelper(this);

        // Initialize PopupMenuHelper with context of your activity
        PopupWindowHelper popupMenuHelper = new PopupWindowHelper(this);

        findViewById(R.id.back_icon).setOnClickListener(v -> {
            // Get the coordinates of the toolbar navigation icon
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            int xOffset = location[0]; // x coordinate
            int yOffset = location[1]; // y coordinate plus the height of the icon

            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v, xOffset, yOffset);
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get an instance of DatabaseHelper
        helper = DatabaseHelper.getInstance();

        // Register this activity as a listener for data changes
        helper.addOnDataChangeListener(this);

        // Call method to retrieve initial data
        helper.retrieveDataAnalysisInitialData(this);

        // Add the AlertsDialogHelper as a listener for database changes
        DatabaseHelper.getInstance().addOnDataChangeListener(alertsDialogHelper::onDatabaseChange);

        // Get references to UI elements
        soilTempTextView = findViewById(R.id.temp_soil);
        airTempTextView = findViewById(R.id.temp_air);
        humidityValue = findViewById(R.id.humidity_value);
        soilMoistureValue = findViewById(R.id.moisture_value);

        soilTempProgressBar = findViewById(R.id.soil_temp_progress_bar);
        airTempProgressBar = findViewById(R.id.air_temp_progress_bar);

        humidityProgressBar = findViewById(R.id.humidity_progress_bar);
        soilMoistureProgressBar = findViewById(R.id.moisture_progress_bar);

        timeText = findViewById(R.id.time_text);

        // Start updating the time
        startClock();

        // Setup line charts
        airLineChart = findViewById(R.id.air_linechart);
        soilLineChart = findViewById(R.id.soil_linechart);

        // Fetch sensor data from Firestore
        fetchDataAndPopulateAirLineChart();
        fetchDataAndPopulateSoilLineChart();
    }


    private void setupLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.parseColor("#FFF0CC"));
        chart.invalidate();
    }

    private void fetchDataAndPopulateAirLineChart() {
        db.collection("sensorHistory")
                .orderBy("timestamp")
                .limitToLast(18)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        List<Entry> airTempEntries = new ArrayList<>();
                        List<Entry> humidityEntries = new ArrayList<>();
                        List<String> timestamps = new ArrayList<>();

                        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yy_HH:mm:ss");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

                        for (QueryDocumentSnapshot document : value) {
                            String timestamp = document.getString("timestamp");
                            double airTemp = document.getDouble("airTemp");
                            double humidity = document.getDouble("humidity");

                            try {
                                Date date = inputFormat.parse(timestamp);
                                String time = outputFormat.format(date);
                                timestamps.add(time);
                                airTempEntries.add(new Entry(airTempEntries.size(), (float) airTemp));
                                humidityEntries.add(new Entry(humidityEntries.size(), (float) humidity));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        LineDataSet airTempDataSet = new LineDataSet(airTempEntries, "Air Temperature");
                        airTempDataSet.setColor(Color.parseColor("#ADD8E6"));
                        airTempDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                        LineDataSet humidityDataSet = new LineDataSet(humidityEntries, "Humidity");
                        humidityDataSet.setColor(Color.parseColor("#FFAD00"));
                        humidityDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                        List<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(airTempDataSet);
                        dataSets.add(humidityDataSet);

                        LineData lineData = new LineData(dataSets);

                        XAxis xAxis = airLineChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        airLineChart.setData(lineData);
                        airLineChart.invalidate();

                        // Setup line charts after setting up data
                        setupLineChart(airLineChart);
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }

    private void fetchDataAndPopulateSoilLineChart() {
        db.collection("sensorHistory")
                .orderBy("timestamp")
                .limitToLast(18)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        List<Entry> soilTempEntries = new ArrayList<>();
                        List<Entry> soilMoistureEntries = new ArrayList<>();
                        List<String> timestamps = new ArrayList<>();

                        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yy_HH:mm:ss");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

                        for (QueryDocumentSnapshot document : value) {
                            String timestamp = document.getString("timestamp");
                            double soilTemp = document.getDouble("soilTemp");
                            double soilMoisture = document.getDouble("soilMoisture");

                            try {
                                Date date = inputFormat.parse(timestamp);
                                String time = outputFormat.format(date);
                                timestamps.add(time);
                                soilTempEntries.add(new Entry(soilTempEntries.size(), (float) soilTemp));
                                soilMoistureEntries.add(new Entry(soilMoistureEntries.size(), (float) soilMoisture));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        LineDataSet soilTempDataSet = new LineDataSet(soilTempEntries, "Soil Temperature");
                        soilTempDataSet.setColor(Color.parseColor("#F44336"));
                        soilTempDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                        LineDataSet soilMoistureDataSet = new LineDataSet(soilMoistureEntries, "Soil Moisture");
                        soilMoistureDataSet.setColor(Color.parseColor("#03A9F4"));
                        soilMoistureDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                        List<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(soilTempDataSet);
                        dataSets.add(soilMoistureDataSet);

                        LineData lineData = new LineData(dataSets);

                        XAxis xAxis = soilLineChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        soilLineChart.setData(lineData);
                        soilLineChart.invalidate();

                        // Setup line chart after setting up data
                        setupLineChart(soilLineChart);
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }


    @Override
    public void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue, double minSoilTempThresh, double maxSoilTempThresh, double minSoilMoistureThresh, double maxSoilMoistureThresh, double minHumidityThresh, double maxHumidityThresh, double minAirTempThresh, double maxAirTempThresh) {

        // Update UI elements based on the received data
        if (soilTempTextView != null) {
            soilTempTextView.setText(String.format("%.1f°C", tempValue));
            soilTempProgressBar.setProgress((int) Math.round(tempValue));
        }

        if (airTempTextView != null) {
            airTempTextView.setText(String.format("%.1f°C", airtempValue));
            airTempProgressBar.setProgress((int) Math.round(airtempValue));
        }

        if (soilMoistureValue != null) {
            String textToDisplay = String.format("%.2f%%", moistureValue);
            soilMoistureValue.setText(textToDisplay);

            // Change color based on thresholds
            int progressColor;
            if (moistureValue > maxSoilMoistureThresh) {
                progressColor = Color.parseColor("#F44336");
            } else if (moistureValue > maxSoilMoistureThresh - 3) { // Adjust threshold for orange color
                progressColor = Color.parseColor("#FFAD00"); // Orange color
            } else if (moistureValue < minSoilMoistureThresh) {
                progressColor = Color.parseColor("#03A9F4"); // Or any color for minimum threshold
            } else if (moistureValue < minSoilMoistureThresh + 3) { // Adjust threshold for a different color
                progressColor = Color.parseColor("#ADD8E6"); // Yellow color for nearing minimum threshold
            } else {
                // Default color when no conditions match
                progressColor = Color.parseColor("#4c6444"); // Default white color
            }

            soilMoistureProgressBar.setProgressColor(progressColor);
            soilMoistureProgressBar.setProgress((int) Math.round(moistureValue));
        }


        if (humidityValue != null) {
            String textToDisplay = String.format("%.2f%%", humidity);
            humidityValue.setText(textToDisplay);

            // Change color based on thresholds
            int progressColor;
            if (humidity > maxHumidityThresh) {
                progressColor = Color.parseColor("#F44336");
            } else if (humidity > maxHumidityThresh - 3) { // Adjust threshold for orange color
                progressColor = Color.parseColor("#FFAD00"); // Orange color
            } else if (humidity < minHumidityThresh) {
                progressColor = Color.parseColor("#03A9F4"); // Or any color for minimum threshold
            } else if (humidity < minHumidityThresh + 3) { // Adjust threshold for a different color
                progressColor = Color.parseColor("#ADD8E6"); // Yellow color for nearing minimum threshold
            } else {
                // Default color when no conditions match
                progressColor = Color.parseColor("#4c6444"); // Default white color
            }

            humidityProgressBar.setProgressColor(progressColor);
            humidityProgressBar.setProgress((int) Math.round(humidity));
        }

    }

        public void updateUIElements(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue,
                                 double minSoilTempThresh, double maxSoilTempThresh,
                                 double minSoilMoistureThresh, double maxSoilMoistureThresh,
                                 double minHumidityThresh, double maxHumidityThresh,
                                 double minAirTempThresh, double maxAirTempThresh) {
        // Call onDatabaseChange with the provided parameters
        onDatabaseChange(humidity, ventiValue, waterValue, moistureValue, tempValue, airtempValue, alertsValue, notifsValue,
                minSoilTempThresh, maxSoilTempThresh,
                minSoilMoistureThresh, maxSoilMoistureThresh,
                minHumidityThresh, maxHumidityThresh,
                minAirTempThresh, maxAirTempThresh);
    }

    private void startClock() {
        // Run a new thread to update the time continuously
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Get the current time
                String currentTime = dateFormat.format(new Date());

                // Update the TextView with the current time
                timeText.setText("As of " + currentTime);

                // Schedule the next update after 1 second
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}

