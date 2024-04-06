package com.allstar.wirwo;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Data_Analysis extends Activity {
    private HalfGauge gauge1;
    private  HalfGauge gauge2;
    private DatabaseReference database;

    private PopupWindowHelper popupMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analytics);

        popupMenuHelper = new PopupWindowHelper(this);


        gauge1 = findViewById(R.id.halfGauge1);
        setupGauge(gauge1);

        gauge2 = findViewById(R.id.halfGauge2);
        setupGauge(gauge2);


        readHumidityData();
        readDsb18Temperature();
        setupLineChart();

        listenToSensorDataChanges();

        findViewById(R.id.toolbar_navigation_icon).setOnClickListener(v -> {
            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v);
        });

    }




    private void readDsb18Temperature() {
        database = FirebaseDatabase.getInstance().getReference("SensorData").child("Soil_Moisture");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double Soil_Moisture = dataSnapshot.getValue(Double.class);
                    updateGauge(gauge2, Soil_Moisture);
                } else {
                    updateGauge(gauge2, 0.0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
            }
        });
    }



    private void readHumidityData() {
        database = FirebaseDatabase.getInstance().getReference("SensorData").child("Humidity");



        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double humidity = dataSnapshot.getValue(Double.class);
                    updateGauge(gauge1, humidity);
                } else {
                    updateGauge(gauge1, 0.0);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
            }
        });
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

    private void updateGauge(HalfGauge gauge, double data) {
        gauge.setValue((float) data);
    }

    private void setupLineChart() {
        // Customize chart appearance
        LineChart mpLineChart = findViewById(R.id.lineChart1);
        int leftPadding = 10;
        int topPadding = 20;
        int rightPadding = 30;
        int bottomPadding = 40;

        mpLineChart.setDrawBorders(true);
        mpLineChart.setBorderWidth(3);
        mpLineChart.setBackgroundColor(Color.parseColor("#4C6444"));
        mpLineChart.setBorderColor(Color.BLACK);
        mpLineChart.setBackgroundColor(Color.WHITE);
        mpLineChart.setDrawGridBackground(true);
        mpLineChart.setExtraOffsets(leftPadding, topPadding, rightPadding, bottomPadding);

        // Set up listeners to update chart data when Firebase data changes
        setupHumidityDataListener(mpLineChart);
        setupTemperatureDataListener(mpLineChart);

        // Customize X-axis
        XAxis xAxis = mpLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Set X-axis position
        xAxis.setGranularity(30); // Set X-axis granularity
        xAxis.setValueFormatter(new TimeAxisValueFormatter());// Set custom X-axis value formatter

        // Customize Y-axis
        YAxis yAxisLeft = mpLineChart.getAxisLeft();
        yAxisLeft.setTextColor(Color.BLACK); // Set Y-axis label color
        yAxisLeft.setAxisMinimum(0f); // Set minimum Y-axis value

        // Hide right Y-axis
        mpLineChart.getAxisRight().setEnabled(false);

        // Customize chart legend
        Legend legend = mpLineChart.getLegend();
        legend.setTextColor(Color.BLACK); // Set legend text color
        legend.setForm(Legend.LegendForm.LINE);

        mpLineChart.invalidate();
    }

    private void setupHumidityDataListener(final LineChart chart) {
        DatabaseReference humidityRef = FirebaseDatabase.getInstance().getReference("SensorData").child("Humidity");
        humidityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> entries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double humidity = snapshot.getValue(Double.class);
                    // Add the humidity value to the entries list
                    assert humidity != null;
                    entries.add(new Entry(entries.size(), humidity.floatValue()));
                }
                // Update the chart data with the new humidity values
                LineDataSet dataSet = new LineDataSet(entries, "Humidity");
                updateLineChart(chart, dataSet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading humidity data: " + databaseError.getMessage());
            }
        });
    }

    private void setupTemperatureDataListener(final LineChart chart) {
        DatabaseReference temperatureRef = FirebaseDatabase.getInstance().getReference("SensorData").child("Temperature_DS18B20");
        temperatureRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> entries = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double temperature = snapshot.getValue(Double.class);
                    // Add the temperature value to the entries list
                    assert temperature != null;
                    entries.add(new Entry(entries.size(), temperature.floatValue()));
                }
                // Update the chart data with the new temperature values
                LineDataSet dataSet = new LineDataSet(entries, "Temperature");
                updateLineChart(chart, dataSet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading temperature data: " + databaseError.getMessage());
            }
        });
    }

    private void updateLineChart(LineChart chart, LineDataSet dataSet) {
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }


    // Custom value formatter for X-axis
    private static class TimeAxisValueFormatter extends ValueFormatter {
        @SuppressLint("DefaultLocale")
        @Override
        public String getFormattedValue(float value) {
            // Convert value to minutes
            long minutes = (long) value;
            // Calculate the hours and minutes
            long hours = minutes / 60;
            long minutesRemainder = minutes % 60;
            // Format the time
            return String.format("%02d:%02d", hours, minutesRemainder);
        }
    }

    private void listenToSensorDataChanges() {
        // Get the Firebase database reference
        DatabaseReference sensorDataRef = FirebaseDatabase.getInstance().getReference().child("SensorData");

        // Add a ValueEventListener to listen for changes in the SensorData node
        sensorDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the dataSnapshot has children
                if (dataSnapshot.exists()) {
                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();
                    // Format the current date into a human-readable format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(currentDate);
                    // Check if the Temperature child node exists
                    if (dataSnapshot.hasChild("Temperature")) {
                        // Get the Temperature value
                        Double temperature = dataSnapshot.child("Temperature").getValue(Double.class);
                        if (temperature != null) {
                            // Update progress bar 1 with the temperature value
                            ProgressBar progressBar1 = findViewById(R.id.vertical_progressbar1);
                            progressBar1.setProgress(temperature.intValue()); // Assuming temperature is in range of 0-100
                            TextView airTempText = findViewById(R.id.temp_air);
                            airTempText.setText(String.valueOf(temperature + " °C"));
                        }
                    }

                    // Check if the Temperature_DS18B20 child node exists
                    if (dataSnapshot.hasChild("Temperature_DS18B20")) {
                        // Get the Temperature_DS18B20 value
                        Double temperatureDs18b20 = dataSnapshot.child("Temperature_DS18B20").getValue(Double.class);
                        if (temperatureDs18b20 != null) {
                            // Update progress bar 2 with the temperature value
                            ProgressBar progressBar2 = findViewById(R.id.vertical_progressbar2);
                            progressBar2.setProgress(temperatureDs18b20.intValue()); // Assuming temperature is in range of 0-100
                            TextView soilTempText = findViewById(R.id.temp_soil);
                            soilTempText.setText(String.valueOf(temperatureDs18b20 + " °C"));
                        }
                    }

                    TextView date_soil = findViewById(R.id.date_soil);
                    TextView date_air = findViewById(R.id.date_air);

                    date_soil.setText(String.format("As of %s", formattedDate));
                    date_air.setText(String.format("As of %s", formattedDate));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the read operation
                Log.e("FirebaseError", "Error reading from Firebase database: " + databaseError.getMessage());
            }
        });
    }



}