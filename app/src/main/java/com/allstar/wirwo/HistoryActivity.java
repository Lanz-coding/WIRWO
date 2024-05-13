package com.allstar.wirwo;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends Activity {

    private AlertsDialogHelper alertsDialogHelper;
    private DatabaseHelper helper;

    private LineChart ActuatorsActivationChart;
    private LineChart SoilReadingsSummaryChart;

    private LineChart AirReadingsSummaryChart;

    private TextView soilTempText, soilDateText, airTempText, airDateText;

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
        ActuatorsActivationChart = findViewById(R.id.actuators_chart);
        SoilReadingsSummaryChart = findViewById(R.id.SoilSummaryReadings_chart);
        AirReadingsSummaryChart = findViewById(R.id.AirSummaryReadings_chart);

        // Setup line charts
        setupLineChart(ActuatorsActivationChart);
        setupLineChart(SoilReadingsSummaryChart);
        setupLineChart(AirReadingsSummaryChart);


        // Fetch sensor data from Firestore
        fetchDataAndPopulateActuatorsLineChart();
        fetchDataAndPopulateAirLineChart();
        fetchDataAndPopulateSoilLineChart();
    }

    private void setupLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.parseColor("#FFFFFF"));
        chart.invalidate();
    }

    private void fetchDataAndPopulateActuatorsLineChart() {
        db.collection("sensorHistory")
                .orderBy("timestamp")
                .limitToLast(10)
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

                        LineDataSet airTempDataSet = new LineDataSet(airTempEntries, "Water Pump Switch Switch");
                        airTempDataSet.setColor(Color.parseColor("#8A6240"));
                        airTempDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                        LineDataSet humidityDataSet = new LineDataSet(humidityEntries, "Ventilation Switch");
                        humidityDataSet.setColor(Color.parseColor("#4C6444"));
                        humidityDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                        List<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(airTempDataSet);
                        dataSets.add(humidityDataSet);

                        LineData lineData = new LineData(dataSets);

                        XAxis xAxis = ActuatorsActivationChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        ActuatorsActivationChart.setData(lineData);
                        ActuatorsActivationChart.invalidate();

                        // Setup line charts after setting up data
                        setupLineChart(ActuatorsActivationChart);
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }

    private void fetchDataAndPopulateAirLineChart() {
        db.collection("sensorHistory")
                .orderBy("timestamp")
                .limitToLast(10)
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

                        XAxis xAxis = AirReadingsSummaryChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        AirReadingsSummaryChart.setData(lineData);
                        AirReadingsSummaryChart.invalidate();

                        // Setup line charts after setting up data
                        setupLineChart(AirReadingsSummaryChart);
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }
    private void fetchDataAndPopulateSoilLineChart() {
        db.collection("sensorHistory")
                .orderBy("timestamp")
                .limitToLast(10)
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

                        XAxis xAxis = SoilReadingsSummaryChart.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        SoilReadingsSummaryChart.setData(lineData);
                        SoilReadingsSummaryChart.invalidate();

                        // Setup line chart after setting up data
                        setupLineChart(SoilReadingsSummaryChart);
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }



}
