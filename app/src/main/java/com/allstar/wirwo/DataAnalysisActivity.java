package com.allstar.wirwo;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
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

public class DataAnalysisActivity extends Activity implements OnDataChangeListener {
    private AlertsDialogHelper alertsDialogHelper;
    private DatabaseHelper helper;
    private HalfGauge gauge1;
    private HalfGauge gauge2;
    private LineChart lineChart1;
    private LineChart lineChart2;
    private ProgressBar vertical_progressbar1, vertical_progressbar2;
    private TextView soilTempText, soilDateText, airTempText, airDateText;

    // Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analytics);


        // Initialize the AlertsDialogHelper
        alertsDialogHelper = new AlertsDialogHelper(this);

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
        soilTempText = findViewById(R.id.temp_soil);
        soilDateText = findViewById(R.id.date_soil);
        airTempText = findViewById(R.id.temp_air);
        airDateText = findViewById(R.id.date_air);
        vertical_progressbar1 = findViewById(R.id.vertical_progressbar1);
        vertical_progressbar2 = findViewById(R.id.vertical_progressbar2);
        gauge1 = findViewById(R.id.halfGauge1);
        gauge2 = findViewById(R.id.halfGauge2);
        lineChart1 = findViewById(R.id.lineChart1);
        lineChart2 = findViewById(R.id.lineChart2);

        // Setup gauges
        setupGauge(gauge1);
        setupGauge(gauge2);

        // Setup line charts
        setupLineChart(lineChart1);
        setupLineChart(lineChart2);

        // Fetch sensor data from Firestore
        fetchSensorData();
    }

    private void setupGauge(HalfGauge gauge) {
        gauge.setMaxValue(100);
        gauge.setBackgroundColor(Color.WHITE);
        gauge.setMinValue(0);
        gauge.setValue(0);


        Range range1 = new Range();
        range1.setColor(Color.parseColor("#00b20b"));
        range1.setFrom(0.0);
        range1.setTo(24.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#4C6444"));
        range2.setFrom(25.0);
        range2.setTo(74.0);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#204020"));
        range3.setFrom(75.0);
        range3.setTo(100.0);

        gauge.addRange(range1);
        gauge.addRange(range2);
        gauge.addRange(range3);
        gauge.setBackgroundColor(Color.parseColor("#CABA9C"));
    }

    private void setupLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.parseColor("#CABA9C"));
        chart.invalidate();
    }

    private void fetchSensorData() {
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
                        airTempDataSet.setColor(Color.BLUE);
                        airTempDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                        LineDataSet humidityDataSet = new LineDataSet(humidityEntries, "Humidity");
                        humidityDataSet.setColor(Color.GREEN);
                        humidityDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                        List<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(airTempDataSet);
                        dataSets.add(humidityDataSet);

                        LineData lineData = new LineData(dataSets);

                        XAxis xAxis = lineChart1.getXAxis();
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);

                        lineChart1.setData(lineData);
                        lineChart1.invalidate();
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }

    public void onDatabaseChange(double humidity, boolean ventiValue, boolean waterValue, double moistureValue, double tempValue, double airtempValue, boolean alertsValue, boolean notifsValue,
                                 double minSoilTempThresh, double maxSoilTempThresh,
                                 double minSoilMoistureThresh, double maxSoilMoistureThresh,
                                 double minHumidityThresh, double maxHumidityThresh,
                                 double minAirTempThresh, double maxAirTempThresh) {

        // Initialize default color
        int defaultColor = ContextCompat.getColor(DataAnalysisActivity.this, R.color.lighterGreen);
        ColorStateList defaultColorStateList = ColorStateList.valueOf(defaultColor);

        // Initialize ColorStateList for each progress bar
        ColorStateList soilTempColorStateList = defaultColorStateList;
        ColorStateList airTempColorStateList = defaultColorStateList;

        SaveToDB.saveUserData(tempValue, moistureValue, humidity, airtempValue);

        // Update UI elements based on the received data
        if (soilTempText != null) {
            soilTempText.setText(String.format("%.1f", tempValue) + "°C");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date(); // or whatever date you want to format
            String formattedDate = sdf.format(currentDate);
            soilDateText.setText(formattedDate);
            vertical_progressbar1.setProgress((int) Math.round(tempValue)); // Assuming progress bar max is 100

            // Change color based on thresholds
            if (tempValue >= maxSoilTempThresh) {
                soilTempColorStateList = ColorStateList.valueOf(Color.RED);
            } else if (tempValue >= maxSoilTempThresh - 3) { // Adjust threshold for orange color
                soilTempColorStateList = ColorStateList.valueOf(Color.parseColor("#FFA500")); // Orange color
            } else if (tempValue <= minSoilTempThresh) {
                soilTempColorStateList = ColorStateList.valueOf(Color.BLUE); // Or any color for minimum threshold
            } else if (tempValue <= minSoilTempThresh + 3) { // Adjust threshold for a different color
                soilTempColorStateList = ColorStateList.valueOf(Color.parseColor("#FFFF00")); // Yellow color for nearing minimum threshold
            }

            // Set the progress tint list
            vertical_progressbar1.setProgressTintList(soilTempColorStateList);


        }

        if (airTempText != null) {
            airTempText.setText(String.format("%.1f", airtempValue) + "°C");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date(); // or whatever date you want to format
            String formattedDate = sdf.format(currentDate);
            airDateText.setText(formattedDate);
            vertical_progressbar2.setProgress((int) Math.round(airtempValue)); // Assuming progress bar max is 100

            // Change color based on thresholds
            if (tempValue >= maxAirTempThresh) {
                airTempColorStateList = ColorStateList.valueOf(Color.RED);
            } else if (tempValue >= maxAirTempThresh - 3) { // Adjust threshold for orange color
                airTempColorStateList = ColorStateList.valueOf(Color.parseColor("#FFA500")); // Orange color
            } else if (tempValue <= minAirTempThresh) {
                airTempColorStateList = ColorStateList.valueOf(Color.BLUE); // Or any color for minimum threshold
            } else if (tempValue <= minAirTempThresh + 3) { // Adjust threshold for a different color
                airTempColorStateList = ColorStateList.valueOf(Color.parseColor("#FFFF00")); // Yellow color for nearing minimum threshold
            }

            // Set the progress tint list
            vertical_progressbar2.setProgressTintList(airTempColorStateList);

        }

        if (gauge1 != null) {
            gauge1.setValue((int) humidity);
        }

        if (gauge2 != null) {
            gauge2.setValue((int) moistureValue);
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
}
