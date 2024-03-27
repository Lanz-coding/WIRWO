package com.example.wirwo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
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


public class Data_Analysis extends AppCompatActivity {
    private ArcGauge gauge1;
    private ArcGauge gauge2;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_analysis);

        gauge1 = findViewById(R.id.guage1);
        setupGauge(gauge1);

        gauge2 = findViewById(R.id.guage2);
        setupGauge(gauge2);


        readHumidityData();
        readDsb18Temperature();
        setupLineChart();

    }

    private void readDsb18Temperature() {
        database = FirebaseDatabase.getInstance().getReference("SensorData").child("Temperature_DS18B20");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double Temperature_DS18B20 = dataSnapshot.getValue(Double.class);
                    updateGauge(gauge2, Temperature_DS18B20);
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

    private void setupGauge(ArcGauge gauge) {
        gauge.setMaxValue(100);
        gauge.setBackgroundColor(Color.WHITE);
        gauge.setMinValue(0);
        gauge.setValue(0);

        Range range1 = new Range();
        range1.setColor(Color.parseColor("#00b20b"));
        range1.setFrom(0.0);
        range1.setTo(24.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#E3E500"));
        range2.setFrom(25.0);
        range2.setTo(74.0);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#ce0000"));
        range3.setFrom(75.0);
        range3.setTo(100.0);

        gauge.addRange(range1);
        gauge.addRange(range2);
        gauge.addRange(range3);
    }

    private void updateGauge(ArcGauge gauge, double data) {
        gauge.setValue((float) data);
    }

    private void setupLineChart() {
        // Customize chart appearance
        LineChart mpLineChart = findViewById(R.id.LI_chart);
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
}
