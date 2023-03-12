package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private final SensorEventListener mListener = new mSensorListener();
    private MainGameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_GAME);
        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.ball_house);
    }

    public class mSensorListener implements SensorEventListener {
        public void onSensorChanged(SensorEvent event) {
            float axisX = event.values[0];
            float axisY = event.values[1];
            float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY);
            float EPSILON = 0.2f;
            if (omegaMagnitude < EPSILON) {
                axisX = axisY = 0;
            }
            final float []ax = new float[2];
            ax[0] = axisX;
            ax[1] = axisY;
            runOnUiThread(() -> gameView.tryMoveBall(ax[0], ax[1]));
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) { }
    }
}