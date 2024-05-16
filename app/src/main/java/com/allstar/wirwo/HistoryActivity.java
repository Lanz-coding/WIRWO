package com.allstar.wirwo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    HistoryActivity extends Activity {

    private AlertsDialogHelper alertsDialogHelper;
    private DatabaseHelper helper;

    private LineChart soilReadingsSummaryChart;

    private LineChart airReadingsSummaryChart;

    private ImageView downloadIcon;

    // Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);


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



        // Initialize the AlertsDialogHelper
        alertsDialogHelper = new AlertsDialogHelper(this);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get an instance of DatabaseHelper
        helper = DatabaseHelper.getInstance();

        // Add the AlertsDialogHelper as a listener for database changes
        DatabaseHelper.getInstance().addOnDataChangeListener(alertsDialogHelper::onDatabaseChange);

        // Get references to UI elements
        soilReadingsSummaryChart = findViewById(R.id.soil_summary_linechart);
        airReadingsSummaryChart = findViewById(R.id.air_summary_linechart);

        downloadIcon = findViewById(R.id.download_icon);

        // Setup line charts
        setupLineChart(soilReadingsSummaryChart);
        setupLineChart(airReadingsSummaryChart);

        downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showDialogWithTitle(HistoryActivity.this, "Download", "Coming Soon!", null);
            }
        });


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
                .limitToLast(30)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        Map<String, List<Float>> dailyReadingsMap = new HashMap<>();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yy_HH:mm:ss");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

                        for (QueryDocumentSnapshot document : value) {
                            String timestamp = document.getString("timestamp");
                            double airTemp = document.getDouble("airTemp");
                            double humidity = document.getDouble("humidity");

                            try {
                                Date date = inputFormat.parse(timestamp);
                                String day = outputFormat.format(date);

                                // Add or update daily readings
                                List<Float> dailyReadings = dailyReadingsMap.getOrDefault(day, new ArrayList<>());
                                dailyReadings.add((float) airTemp);
                                dailyReadings.add((float) humidity);
                                dailyReadingsMap.put(day, dailyReadings);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        List<Entry> airTempEntries = new ArrayList<>();
                        List<Entry> humidityEntries = new ArrayList<>();
                        List<String> timestamps = new ArrayList<>();

                        // Calculate daily averages and prepare data for chart
                        for (Map.Entry<String, List<Float>> entry : dailyReadingsMap.entrySet()) {
                            String day = entry.getKey();
                            List<Float> readings = entry.getValue();
                            float airTempAvg = calculateAverage(readings.subList(0, readings.size() / 2));
                            float humidityAvg = calculateAverage(readings.subList(readings.size() / 2, readings.size()));

                            airTempEntries.add(new Entry(timestamps.size(), airTempAvg));
                            humidityEntries.add(new Entry(timestamps.size(), humidityAvg));
                            timestamps.add(day);
                        }

                        // Prepare data sets and update chart
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

                        XAxis xAxis = airReadingsSummaryChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        airReadingsSummaryChart.setData(lineData);
                        airReadingsSummaryChart.invalidate();

                        // Setup line charts after setting up data
                        setupLineChart(airReadingsSummaryChart);
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }

    private void fetchDataAndPopulateSoilLineChart() {
        db.collection("sensorHistory")
                .orderBy("timestamp")
                .limitToLast(30)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        Map<String, List<Float>> dailyReadingsMap = new HashMap<>();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yy_HH:mm:ss");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd-yyyy");

                        for (QueryDocumentSnapshot document : value) {
                            String timestamp = document.getString("timestamp");
                            double soilTemp = document.getDouble("soilTemp");
                            double soilMoisture = document.getDouble("soilMoisture");

                            try {
                                Date date = inputFormat.parse(timestamp);
                                String day = outputFormat.format(date);

                                // Add or update daily readings
                                List<Float> dailyReadings = dailyReadingsMap.getOrDefault(day, new ArrayList<>());
                                dailyReadings.add((float) soilTemp);
                                dailyReadings.add((float) soilMoisture);
                                dailyReadingsMap.put(day, dailyReadings);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        List<Entry> soilTempEntries = new ArrayList<>();
                        List<Entry> soilMoistureEntries = new ArrayList<>();
                        List<String> timestamps = new ArrayList<>();

                        // Calculate daily averages and prepare data for chart
                        for (Map.Entry<String, List<Float>> entry : dailyReadingsMap.entrySet()) {
                            String day = entry.getKey();
                            List<Float> readings = entry.getValue();
                            float soilTempAvg = calculateAverage(readings.subList(0, readings.size() / 2));
                            float soilMoistureAvg = calculateAverage(readings.subList(readings.size() / 2, readings.size()));

                            soilTempEntries.add(new Entry(timestamps.size(), soilTempAvg));
                            soilMoistureEntries.add(new Entry(timestamps.size(), soilMoistureAvg));
                            timestamps.add(day);
                        }

                        // Prepare data sets and update chart
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

                        XAxis xAxis = soilReadingsSummaryChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        soilReadingsSummaryChart.setData(lineData);
                        soilReadingsSummaryChart.invalidate();

                        // Setup line chart after setting up data
                        setupLineChart(soilReadingsSummaryChart);
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }


    private float calculateAverage(List<Float> values) {
        float sum = 0;
        for (Float value : values) {
            sum += value;
        }
        return sum / values.size();
    }

}
