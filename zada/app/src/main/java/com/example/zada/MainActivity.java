package com.example.zada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    Stopwatch time;
    int numberOfReboots = 0;
    LocalTime lastBootTime = null, lastFrontTime = null;
    File path = null;
    private String readFrom(String name) throws IOException {
            File file = new File(path, name);
            String res;
            byte[] arr = new byte[20];
            int count = new FileInputStream(file).read(arr);
            if (count == -1) throw new IOException("read return -1");
            return new String(arr, 0, count);
    }
    private boolean logRead() {
        try {
            String val = readFrom("number_of_runs");
            numberOfReboots = Integer.parseInt(val);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        try {
            lastBootTime = LocalTime.parse(readFrom("last_boot_time"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        try {
            lastFrontTime = LocalTime.parse(readFrom("last_front_time"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    private void writeTo(String name, String text) throws IOException {
        File file = new File(path, name);
        new FileOutputStream(file).write(text.getBytes());
    }
    private boolean logWrite() {
        LocalTime quiteTime = LocalTime.now();
        try {
            writeTo("number_of_runs", String.valueOf(numberOfReboots + 1));
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        try {
            writeTo("last_boot_time", quiteTime.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        try {
            writeTo("last_front_time", time.getUsedTime(quiteTime).toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    private String last10chars(String s) {
        if (s.length() < 10) return s;
        return s.substring(0, 10);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        path = getApplicationContext().getFilesDir();
        if (logRead()) {
            TextView boots = findViewById(R.id.launchesCount);
            TextView lastLaunchTime = findViewById(R.id.lastLaunchTimeDifference);
            TextView lastLaunchUsingTime = findViewById(R.id.lastFrontTime);
            boots.setText(String.valueOf(numberOfReboots));
            lastLaunchTime.setText(last10chars(addDiference(LocalTime.MIN, LocalTime.now(), lastBootTime).toString()));
            lastLaunchUsingTime.setText(last10chars(lastFrontTime.toString()));
        } else {
            TextView boots = findViewById(R.id.launchesCount);
            TextView lastLaunchTime = findViewById(R.id.lastLaunchTimeDifference);
            TextView lastLaunchUsingTime = findViewById(R.id.lastFrontTime);
            boots.setText("0");
            lastLaunchTime.setText("It's your first launch!!");
            lastLaunchUsingTime.setText("Congratulations!!");
        }
        time = new Stopwatch(LocalTime.now());
        time.resume(LocalTime.now());
        getLifecycle().addObserver(time);
    }
    public LocalTime addDiference(LocalTime res, LocalTime first, LocalTime second) {
        res = res.plusNanos(second.until(first, ChronoUnit.NANOS));
        return res;
    }
    public class Stopwatch implements DefaultLifecycleObserver {
        private LocalTime timeInUse;
        private LocalTime lastCheckTime;
        private LocalTime lastRunTime;
        final Handler handler = new Handler();
        final int delay = 100;
        final Runnable r = new Runnable() {
            public void run() {
                if (lastCheckTime != null) {
                    TextView timeSinceReboot = findViewById(R.id.timeFromLaunch);
                    TextView frontTime = findViewById(R.id.frontTime);
                    timeSinceReboot.setText(last10chars(addDiference(LocalTime.MIN, LocalTime.now(), lastRunTime).toString()));
                    updateFrontTime(LocalTime.now());
                    frontTime.setText(last10chars(timeInUse.toString()));
                    handler.postDelayed(r, 100);
                } else {
                    //System.err.println("null");
                }
            }
        };
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            resume(LocalTime.now());
            handler.postDelayed(r, 1);
        }
        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            stop(LocalTime.now());
        }
        public Stopwatch(LocalTime actualTime) {
            timeInUse = LocalTime.MIN;
            lastCheckTime = LocalTime.MIN;
            lastRunTime = actualTime;
            handler.postDelayed(r, 100);
        }
        public void resume(LocalTime actualTime) {
            lastCheckTime = actualTime;
        }
        public void stop(LocalTime actualTime) {
            Objects.requireNonNull(lastCheckTime);
            timeInUse = addDiference(timeInUse, actualTime, lastCheckTime);
            lastCheckTime = null;
        }
        public void updateFrontTime(LocalTime actualTime) {
            Objects.requireNonNull(lastCheckTime);
            timeInUse = addDiference(timeInUse, actualTime, lastCheckTime);
            lastCheckTime = actualTime;
        }
        public LocalTime getUsedTime(LocalTime actualTime) {
            if (lastCheckTime != null) {
                updateFrontTime(actualTime);
            }
            return timeInUse;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        logWrite();
    }
}