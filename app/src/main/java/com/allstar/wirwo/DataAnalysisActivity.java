package com.allstar.wirwo;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataAnalysisActivity extends Activity {
    private AlertsDialogHelper alertsDialogHelper;
    private HalfGauge gauge1;
    private HalfGauge gauge2;
    private LineChart lineChart1;
    private LineChart lineChart2;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analytics);
        // Initialize the AlertsDialogHelper
        alertsDialogHelper = new AlertsDialogHelper(this);

        // Add the AlertsDialogHelper as a listener for database changes
        DatabaseHelper.getInstance().addOnDataChangeListener(alertsDialogHelper::onDatabaseChange);

        // Initialize PopupMenuHelper with context of your activity
        PopupWindowHelper popupMenuHelper = new PopupWindowHelper(this);

        findViewById(R.id.back_icon).setOnClickListener(v -> {
            // Call showPopup() method to show the popup
            popupMenuHelper.showPopup(v);
        });


        gauge1 = findViewById(R.id.halfGauge1);
        setupGauge(gauge1);

        gauge2 = findViewById(R.id.halfGauge2);
        setupGauge(gauge2);

        lineChart1 = findViewById(R.id.lineChart1);
        setupLineChart(lineChart1);

        lineChart2 = findViewById(R.id.lineChart2);
        setupLineChart(lineChart2);

        List<Entry> samplePoints = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            samplePoints.add(new Entry(i, i*2));
        }

        LineDataSet sampleDataSet = new LineDataSet(samplePoints, "sample data");
        sampleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        List<ILineDataSet> iLineDataSets = new ArrayList<>();
        iLineDataSets.add(sampleDataSet);
        LineData sampleLineData = new LineData(iLineDataSets);
        lineChart1.setData(sampleLineData);

//        readHumidityData();
//        readDsb18Temperature();
//        listenToSensorDataChanges();
    }

    private void readDsb18Temperature() {
        database = FirebaseDatabase.getInstance().getReference("SensorData").child("Temperature_DS18B20");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double temperature = dataSnapshot.getValue(Double.class);
                    updateGauge(gauge2, temperature);
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

    private void setupLineChart(LineChart chart) {
        chart.getDescription().setEnabled(true);
//        chart.setTouchEnabled(true);
//        chart.setDragEnabled(true);
//        chart.setScaleEnabled(false);
//        chart.setPinchZoom(false);
//        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.parseColor("#CABA9C"));

//        LineData lineData = chart.getData();
//        if (lineData == null) {
//            lineData = new LineData();
//            chart.setData(lineData);
//        }
//
//        if (chart.getId() == R.id.lineChart1) {
//            ILineDataSet dataSet1 = lineData.getDataSetByIndex(0);
//            if (dataSet1 == null) {
//                dataSet1 = createLineDataSet("Air Temperature", Color.BLUE);
//                lineData.addDataSet(dataSet1);
//            }
//        } else if (chart.getId() == R.id.lineChart2) {
//            ILineDataSet dataSet2 = lineData.getDataSetByIndex(1);
//            if (dataSet2 == null) {
//                dataSet2 = createLineDataSet("Soil Moisture", Color.GREEN);
//                lineData.addDataSet(dataSet2);
//            }
//        }

        chart.invalidate();
    }

    private LineDataSet createLineDataSet(String label, int color) {
        LineDataSet dataSet = new LineDataSet(null, label);
        dataSet.setLineWidth(2f);
        dataSet.setColor(color);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(color);
        dataSet.setDrawValues(false);
        return dataSet;
    }


    private void listenToSensorDataChanges() {
        DatabaseReference sensorDataRef = FirebaseDatabase.getInstance().getReference("SensorData");
        sensorDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    new FirebaseDataAsyncTask().execute(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error reading data: " + databaseError.getMessage());
            }
        });
    }

    private class FirebaseDataAsyncTask extends AsyncTask<DataSnapshot, Void, Void> {
        @Override
        protected Void doInBackground(DataSnapshot... snapshots) {
            DataSnapshot snapshot = snapshots[0];

            if (snapshot.exists()) {
                // Extract sensor data from snapshot
                Double airTemp = snapshot.child("Temperature").getValue(Double.class);
                Double humidity = snapshot.child("Humidity").getValue(Double.class);
                Double soilTemp = snapshot.child("Temperature_DS18B20").getValue(Double.class);
                Double soilMoisture = snapshot.child("Soil_Moisture").getValue(Double.class);

                // Update line charts on the main thread
                runOnUiThread(() -> {
                    // Update line chart 1 (Air Temp and Humidity)
                    LineData lineData1 = lineChart1.getLineData();
                    if (lineData1 != null) {
                        ILineDataSet dataSet1 = lineData1.getDataSetByIndex(0);
                        if (dataSet1 != null) {
                            lineData1.addEntry(new Entry(dataSet1.getEntryCount(), airTemp.floatValue()), 0);
                            lineData1.addEntry(new Entry(dataSet1.getEntryCount(), humidity.floatValue()), 1);
                            lineData1.notifyDataChanged();
                            lineChart1.notifyDataSetChanged();
                            lineChart1.invalidate();
                        }
                    }

                    // Update line chart 2 (Soil Temp and Soil Moisture)
                    LineData lineData2 = lineChart2.getLineData();
                    if (lineData2 != null) {
                        ILineDataSet dataSet2 = lineData2.getDataSetByIndex(0);
                        if (dataSet2 != null) {
                            lineData2.addEntry(new Entry(dataSet2.getEntryCount(), soilTemp.floatValue()), 0);
                            lineData2.addEntry(new Entry(dataSet2.getEntryCount(), soilMoisture.floatValue()), 1);
                            lineData2.notifyDataChanged();
                            lineChart2.notifyDataSetChanged();
                            lineChart2.invalidate();
                        }
                    }
                });
            }
            return null;
        }
    }
}
