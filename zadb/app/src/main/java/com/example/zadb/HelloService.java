package com.example.zadb;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HelloService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private String path;
    public void showNotification2(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                "YOUR_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
        mNotificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
    List<String> urls = new ArrayList<>();
    List<String> content = new ArrayList<>();
    private void loadAddresses() {
        try {
            String[] allUrls = Utility.readFrom(new File(path), "allNames").split("\n");
            for (String url : allUrls) {
                if (!url.equals("")) {
                    urls.add(url);
                    try {
                        String urlContent = Utility.readFrom(new File(path), Utility.getFoldUrl(url));
                        content.add(urlContent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch(IOException ignored) {}
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {

            for (int i = 0; i < urls.size(); ++i) {
                try {
                    String contentUrl = Utility.loadUrlContent(urls.get(i));
                    if (!contentUrl.equals(content.get(i))) {
                        showNotification2("Content changed!", "New content on " + urls.get(i));
                        content.set(i, contentUrl);
                        Utility.writeTo(new File(path), Utility.getFoldUrl(urls.get(i)), contentUrl);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onCreate() {
        //Toast.makeText(this, "creating background url's tracker", Toast.LENGTH_SHORT).show();
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        path = intent.getExtras().getString("path_name");
        loadAddresses();

        Thread t = new Thread(() -> {
            Message msg = serviceHandler.obtainMessage();
            serviceHandler.sendMessage(msg);
        });
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(t, 0, 5, TimeUnit.SECONDS);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
