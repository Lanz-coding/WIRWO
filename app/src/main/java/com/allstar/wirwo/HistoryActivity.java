package com.allstar.wirwo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;


public class    HistoryActivity extends Activity {

    private AlertsDialogHelper alertsDialogHelper;
    private DatabaseHelper helper;

    private LineChart soilReadingsSummaryChart, airReadingsSummaryChart;

    private TextView soilTime, airTime;

    private ImageView downloadIcon;

    private static final int REQUEST_CODE = 1232;

    // Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        askForPermission();


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

        soilTime = findViewById(R.id.soil_range_text);
        airTime = findViewById(R.id.air_range_text);

        // Setup line charts
        setupLineChart(soilReadingsSummaryChart);
        setupLineChart(airReadingsSummaryChart);

        downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertDataToPDF();
            }
        });


        // Fetch sensor data from Firestore
        fetchDataAndPopulateAirLineChart();
        fetchDataAndPopulateSoilLineChart();
    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    private void convertDataToPDF() {
        // Get the current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy_HH-mm-ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());

        // Construct the filename with the desired format
        String filename = "WirWo_Readings_" + timestamp + ".pdf";

        db.collection("sensorHistory")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        SensorSummary sensorSummary = generateSummary(documents);
                        formatSummaryDataToPDF(sensorSummary, filename);
                    } else {
                        Toast.makeText(this, "No data found in Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch data from Firestore", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error fetching data", e);
                });
    }

    private void formatSummaryDataToPDF(SensorSummary summary, String filename) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File summaryFile = new File(downloadsDir, filename);

        try {
            PdfWriter writer = new PdfWriter(summaryFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_wirwo_transparent, null);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = new Image(ImageDataFactory.create(stream.toByteArray()));
            image.setWidth(70);
            image.setHeight(70);
            image.setHorizontalAlignment(HorizontalAlignment.CENTER);

            document.add(image);


            Paragraph appName = new Paragraph("WiRWO")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(0, 128, 64))
                    .setBold()
                    .setFontSize(24);

            document.add(appName);

            Paragraph title = new Paragraph("Readings Summary")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(0, 128, 64))
                    .setBold()
                    .setFontSize(20)
                    .setMarginBottom(2);

            document.add(title);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String timestamp = dateFormat.format(new Date());
            document.add(new Paragraph("As of " + timestamp)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20)
                    .setItalic());

            float[] columnsWidth = {200f, 200f};
            Table table = new Table(columnsWidth);

            table.addCell(new Cell()
                    .add(new Paragraph("Property"))
                    .setFontColor(new DeviceRgb(255, 255, 255))
                    .setBackgroundColor(new DeviceRgb(0, 128, 64))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold());

            table.addCell(new Cell().add(new Paragraph("Value"))
                    .setFontColor(new DeviceRgb(255, 255, 255))
                    .setBackgroundColor(new DeviceRgb(0, 128, 64))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold());

            table.addCell(new Cell().add(new Paragraph("Average Air Temperature")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f", summary.getAverageAirTemp())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Average Humidity")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getAverageHumidity())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Average Soil Temperature")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getAverageSoilTemp())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Average Soil Moisture")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getAverageSoilMoisture())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Max Air Temperature")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getMaxAirTemp())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Max Humidity")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getMaxHumidity())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Max Soil Temperature")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getMaxSoilTemp())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Max Soil Moisture")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getMaxSoilMoisture())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Min Air Temperature")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getMinAirTemp())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Min Humidity")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getMinHumidity())))
                    .setTextAlignment(TextAlignment.CENTER));

            table.addCell(new Cell().add(new Paragraph("Min Soil Temperature")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getMinSoilTemp())))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("Min Soil Moisture")));
            table.addCell(new Cell().add(new Paragraph(String.format(Locale.US, "%.2f",summary.getMinSoilMoisture())))
                    .setTextAlignment(TextAlignment.CENTER));
            table.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setBorderRadius(new BorderRadius(20));

            document.add(table);

            document.close();
            DialogHelper.showDialogWithTitle(this,"PDF created successfully!" ,"Filename: \n" + filename + "\nSee your Downloads Folder.", null);
        } catch (FileNotFoundException e) {
            Log.e("PDF", "File not found.", e);
            throw new RuntimeException(e);
        }
    }

    private void setupLineChart(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setBackgroundColor(Color.parseColor("#FFF0CC"));
        chart.invalidate();
    }

    private void fetchDataAndPopulateAirLineChart() {
        db.collection("sensorHistory")
                .orderBy("timestamp") // Sort data by timestamp
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        Map<String, List<Float>> dailyReadingsMap = new TreeMap<>(); // Use TreeMap to automatically sort by key (timestamp)
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

                        // Set text for airTime to show the date range
                        if (!timestamps.isEmpty()) {
                            String startDate = timestamps.get(0);
                            String endDate = timestamps.get(timestamps.size() - 1);
                            runOnUiThread(() -> airTime.setText(String.format("Date Range: %s - %s", startDate, endDate)));
                        }

                        // Setup line charts after setting up data
                        setupLineChart(airReadingsSummaryChart);
                    } else {
                        Log.d("Firestore", "No data found.");
                    }
                });
    }

    private void fetchDataAndPopulateSoilLineChart() {
        db.collection("sensorHistory")
                .orderBy("timestamp") // Sort data by timestamp
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        Map<String, List<Float>> dailyReadingsMap = new TreeMap<>(); // Use TreeMap to automatically sort by key (timestamp)
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

                        // Set text for soilTime to show the date range
                        if (!timestamps.isEmpty()) {
                            String startDate = timestamps.get(0);
                            String endDate = timestamps.get(timestamps.size() - 1);
                            runOnUiThread(() -> soilTime.setText(String.format("Date Range: %s - %s", startDate, endDate)));
                        }

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
    private SensorSummary generateSummary(List<DocumentSnapshot> documents) {
        int count = documents.size();

        double sumAirTemp = 0.0;
        double sumHumidity = 0.0;
        double sumSoilTemp = 0.0;
        double sumSoilMoisture = 0.0;

        double maxAirTemp = Double.MIN_VALUE;
        double maxHumidity = Double.MIN_VALUE;
        double maxSoilTemp = Double.MIN_VALUE;
        double maxSoilMoisture = Double.MIN_VALUE;

        double minAirTemp = Double.MAX_VALUE;
        double minHumidity = Double.MAX_VALUE;
        double minSoilTemp = Double.MAX_VALUE;
        double minSoilMoisture = Double.MAX_VALUE;

        for (DocumentSnapshot document : documents) {
            double airTemp = document.getDouble("airTemp");
            double humidity = document.getDouble("humidity");
            double soilTemp = document.getDouble("soilTemp");
            double soilMoisture = document.getDouble("soilMoisture");

            // Sum for calculating average
            sumAirTemp += airTemp;
            sumHumidity += humidity;
            sumSoilTemp += soilTemp;
            sumSoilMoisture += soilMoisture;

            // Update maximum values
            maxAirTemp = Math.max(maxAirTemp, airTemp);
            maxHumidity = Math.max(maxHumidity, humidity);
            maxSoilTemp = Math.max(maxSoilTemp, soilTemp);
            maxSoilMoisture = Math.max(maxSoilMoisture, soilMoisture);

            // Update minimum values
            minAirTemp = Math.min(minAirTemp, airTemp);
            minHumidity = Math.min(minHumidity, humidity);
            minSoilTemp = Math.min(minSoilTemp, soilTemp);
            minSoilMoisture = Math.min(minSoilMoisture, soilMoisture);
        }

        // Calculate averages
        double avgAirTemp = sumAirTemp / count;
        double avgHumidity = sumHumidity / count;
        double avgSoilTemp = sumSoilTemp / count;
        double avgSoilMoisture = sumSoilMoisture / count;

        // Create and return SensorSummary object
        return new SensorSummary(avgAirTemp, avgHumidity, avgSoilTemp, avgSoilMoisture,
                maxAirTemp, maxHumidity, maxSoilTemp, maxSoilMoisture,
                minAirTemp, minHumidity, minSoilTemp, minSoilMoisture);
    }


}



 class SensorSummary {
    private final double averageAirTemp;
    private final double averageHumidity;
    private final double averageSoilTemp;
    private final double averageSoilMoisture;

    private final double maxAirTemp;
    private final double maxHumidity;
    private final double maxSoilTemp;
    private final double maxSoilMoisture;

    private final double minAirTemp;
    private final double minHumidity;
    private final double minSoilTemp;
    private final double minSoilMoisture;

    public SensorSummary(double averageAirTemp, double averageHumidity, double averageSoilTemp, double averageSoilMoisture,
                         double maxAirTemp, double maxHumidity, double maxSoilTemp, double maxSoilMoisture,
                         double minAirTemp, double minHumidity, double minSoilTemp, double minSoilMoisture) {
        this.averageAirTemp = averageAirTemp;
        this.averageHumidity = averageHumidity;
        this.averageSoilTemp = averageSoilTemp;
        this.averageSoilMoisture = averageSoilMoisture;
        this.maxAirTemp = maxAirTemp;
        this.maxHumidity = maxHumidity;
        this.maxSoilTemp = maxSoilTemp;
        this.maxSoilMoisture = maxSoilMoisture;
        this.minAirTemp = minAirTemp;
        this.minHumidity = minHumidity;
        this.minSoilTemp = minSoilTemp;
        this.minSoilMoisture = minSoilMoisture;
    }

    public double getAverageAirTemp() {
        return averageAirTemp;
    }

    public double getAverageHumidity() {
        return averageHumidity;
    }

    public double getAverageSoilTemp() {
        return averageSoilTemp;
    }

    public double getAverageSoilMoisture() {
        return averageSoilMoisture;
    }

    public double getMaxAirTemp() {
        return maxAirTemp;
    }

    public double getMaxHumidity() {
        return maxHumidity;
    }

    public double getMaxSoilTemp() {
        return maxSoilTemp;
    }

    public double getMaxSoilMoisture() {
        return maxSoilMoisture;
    }

    public double getMinAirTemp() {
        return minAirTemp;
    }

    public double getMinHumidity() {
        return minHumidity;
    }

    public double getMinSoilTemp() {
        return minSoilTemp;
    }

    public double getMinSoilMoisture() {
        return minSoilMoisture;
    }
}

