package com.example.stepappv4;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

// TODO 5: Create a class that implements SensorEventListener interface
public class  StepCounterListener implements SensorEventListener {

    private long lastSensorUpdate = 0;
    public static int accStepCounter = 0;
    ArrayList<Integer> accSeries = new ArrayList<Integer>();
    private double accMag = 0;
    private int lastAddedIndex = 1;
    int stepThreshold = 6;
    private  Context context;
    //TODO 13: Declare the TextView in the listener class
    TextView stepCountsView;
    //TODO 16 (Your Turn): Declare the CircularProgressIndicator in the listener class

    //TODO 14: Pass the TextView to the listener class using the constructor
    //TODO 17 (Your Turn): Add the CircularProgressIndicator as a paramter in the constructor

    public StepCounterListener(Context context, TextView stepCountsView)
    {
        this.stepCountsView = stepCountsView;
        this.context = context;
        //TODO 18 (Your Turn): Assign the CircularProgressIndicator variable

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // TODO 8: Check the type of the sensor, this is helpful in case of multiple sensors (you will need for the next assignment)
        switch (sensorEvent.sensor.getType())
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:

                // TODO 9: Get the raw acc. sensor data
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                // TODO 10: Log the raw acc. sensor data and the event timestamp

                long currentTimeInMilliSecond = System.currentTimeMillis();
                long timeUntilSensorEvent =(SystemClock.elapsedRealtimeNanos()  - sensorEvent.timestamp )/1000000;

                long SensorEventTimestampInMilliSecond =  currentTimeInMilliSecond - timeUntilSensorEvent;

                SimpleDateFormat sensorEventTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sensorEventTimestamp.setTimeZone(TimeZone.getTimeZone("GMT+2"));

                String sensorEventDate = sensorEventTimestamp.format(SensorEventTimestampInMilliSecond);


                if ((currentTimeInMilliSecond - lastSensorUpdate) > 1000)
                {
                    lastSensorUpdate = currentTimeInMilliSecond;
                    String sensorRawValues = "  x = "+ String.valueOf(x) +"  y = "+ String.valueOf(y) +"  z = "+ String.valueOf(z);
                    Log.d("Acc. Event", "last sensor update at " + String.valueOf(sensorEventDate) + sensorRawValues);
                }

                // TODO 11 (YOUR TURN): Compute the magnitude for the acceleration and put it in accMag


                // TODO 12 (YOUR TURN): Store the magnitude for the acceleration in accSeries


                peakDetection();

                break;

            case Sensor.TYPE_STEP_DETECTOR:
                // TODO (Assignment 02): Use the STEP_DETECTOR  to count the number of steps
                // TODO (Assignment 02): The STEP_DETECTOR is triggered every time a step is detected
                // TODO (Assignment 02): The sensorEvent.values of STEP_DETECTOR has only one value for the detected step count

        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void peakDetection() {

        int windowSize = 20;
        /* Peak detection algorithm derived from: A Step Counter Service for Java-Enabled Devices Using a Built-In Accelerometer Mladenov et al.
         */
        int currentSize = accSeries.size(); // get the length of the series
        if (currentSize - lastAddedIndex < windowSize)
        {
            // if the segment is smaller than the processing window size skip it
            return;
        }

        List<Integer> valuesInWindow = accSeries.subList(lastAddedIndex,currentSize);
        lastAddedIndex = currentSize;

        for (int i = 1; i < valuesInWindow.size()-1; i++) {
            int forwardSlope = valuesInWindow.get(i + 1) - valuesInWindow.get(i);
            int downwardSlope = valuesInWindow.get(i) - valuesInWindow.get(i - 1);

            if (forwardSlope < 0 && downwardSlope > 0 && valuesInWindow.get(i) > stepThreshold) {
                accStepCounter += 1;
                Log.d("ACC STEPS: ", String.valueOf(accStepCounter));

                //TODO 15: Update the TextView with the number of steps calculated using ACC. sensor
                stepCountsView.setText(String.valueOf(accStepCounter));

                //TODO 17: Add the new steps to the database
                saveStepInDatabase();

                //TODO 19 (Your Turn): Set the progress of the CircularProgressIndicator variable



            }
        }
    }

    private void countSteps(float step)
    {

    }
    private void saveStepInDatabase()
    {
        //get current Timestamp
        long timeInMillis = System.currentTimeMillis();
        // Convert the timestamp to yyyy-MM-dd HH:mm:ss:SSS format
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        final String dateTimestamp = jdf.format(timeInMillis);
        String currentDay = dateTimestamp.substring(0,10);
        String hour = dateTimestamp.substring(11,13);



        ContentValues values = new ContentValues();
        values.put(StepAppOpenHelper.KEY_TIMESTAMP, dateTimestamp);
        values.put(StepAppOpenHelper.KEY_DAY, currentDay);
        values.put(StepAppOpenHelper.KEY_HOUR, hour);

        //Get the writable database
        StepAppOpenHelper databaseOpenHelper =   new StepAppOpenHelper(this.context);;
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();
        long id = database.insert(StepAppOpenHelper.TABLE_NAME, null, values);
    }


}