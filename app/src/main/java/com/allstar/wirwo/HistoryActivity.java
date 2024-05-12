//package com.allstar.wirwo;
//
//import android.app.Activity;
//import android.content.res.ColorStateList;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.TextView;
//
//import androidx.core.content.ContextCompat;
//
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class HistoryActivity extends Activity {
//
//    private AlertsDialogHelper alertsDialogHelper;
//    private DatabaseHelper helper;
//
//    private LineChart ActuatorsActivationChart;
//    private LineChart ReadingsSummaryChart;
//
//    private TextView soilTempText, soilDateText, airTempText, airDateText;
//
//    // Firestore
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_data_analytics);
//
//
//        // Initialize the AlertsDialogHelper
//        alertsDialogHelper = new AlertsDialogHelper(this);
//
//        // Initialize Firestore
//        db = FirebaseFirestore.getInstance();
//
//        // Get an instance of DatabaseHelper
//        helper = DatabaseHelper.getInstance();
//
//        // Add the AlertsDialogHelper as a listener for database changes
//        DatabaseHelper.getInstance().addOnDataChangeListener(alertsDialogHelper::onDatabaseChange);
//
//        // Get references to UI elements
//        soilTempText = findViewById(R.id.temp_soil);
//        soilDateText = findViewById(R.id.date_soil);
//        airTempText = findViewById(R.id.temp_air);
//        airDateText = findViewById(R.id.date_air);
//        ActuatorsActivationChart = findViewById(R.id.actuators_chart);
//        ReadingsSummaryChart = findViewById(R.id.readings_chart);
//
//        // Setup line charts
//        setupLineChart(ActuatorsActivationChart);
//        setupLineChart(ReadingsSummaryChart);
//
//        // Fetch sensor data from Firestore
//        fetchSensorData();
//    }
//
//    private void setupLineChart(LineChart chart) {
//        chart.getDescription().setEnabled(false);
//        chart.setBackgroundColor(Color.parseColor("#CABA9C"));
//        chart.invalidate();
//    }
//
//    private void fetchSensorData() {
//        db.collection("sensorHistory")
//                .orderBy("timestamp")
//                .limitToLast(10)
//                .addSnapshotListener((value, error) -> {
//                    if (error != null) {
//                        Log.e("Firestore", "Listen failed.", error);
//                        return;
//                    }
//
//                    if (value != null && !value.isEmpty()) {
//                        List<Entry> airTempEntries = new ArrayList<>();
//                        List<Entry> humidityEntries = new ArrayList<>();
//                        List<String> timestamps = new ArrayList<>();
//
//                        SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yy_HH:mm:ss");
//                        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
//
//                        for (QueryDocumentSnapshot document : value) {
//                            String timestamp = document.getString("timestamp");
//                            double airTemp = document.getDouble("airTemp");
//                            double humidity = document.getDouble("humidity");
//
//                            try {
//                                Date date = inputFormat.parse(timestamp);
//                                String time = outputFormat.format(date);
//                                timestamps.add(time);
//                                airTempEntries.add(new Entry(airTempEntries.size(), (float) airTemp));
//                                humidityEntries.add(new Entry(humidityEntries.size(), (float) humidity));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        LineDataSet airTempDataSet = new LineDataSet(airTempEntries, "Air Temperature");
//                        airTempDataSet.setColor(Color.BLUE);
//                        airTempDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//
//                        LineDataSet humidityDataSet = new LineDataSet(humidityEntries, "Humidity");
//                        humidityDataSet.setColor(Color.GREEN);
//                        humidityDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//
//                        List<ILineDataSet> dataSets = new ArrayList<>();
//                        dataSets.add(airTempDataSet);
//                        dataSets.add(humidityDataSet);
//
//                        LineData lineData = new LineData(dataSets);
//
//                        XAxis xAxis = ActuatorsActivationChart.getXAxis();
//                        xAxis.setValueFormatter(new IndexAxisValueFormatter(timestamps));
//                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//                        xAxis.setGranularity(1f);
//
//                        ActuatorsActivationChart.setData(lineData);
//                        ActuatorsActivationChart.invalidate();
//                    } else {
//                        Log.d("Firestore", "No data found.");
//                    }
//                });
//    }
//}
//
