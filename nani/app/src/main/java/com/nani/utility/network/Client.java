package com.nani.utility.network;

import java.util.LinkedList;
import java.util.Queue;

public class Client {
    private final ConnectionManager connectionManager;
    private final Queue <String> toRead;
    private final Queue <String> toWrite;

    public interface ReadingRequests {
        void process(String line);
    }
    private ReadingRequests readCallback;

    public Client() {
        this.connectionManager = new ConnectionManager();
        this.toRead = new LinkedList<>();
        this.toWrite = new LinkedList<>();
    }

    public boolean run() {
        if (!connectionManager.connect())
            return false;
        Thread tRead = new Thread(() -> {
            while(connectionManager.isConnected()) {
                String line = connectionManager.readLine();
                toRead.add(line);
                tryProcessReading();
            }
        });
        tRead.start();
        Thread tWrite = new Thread(() -> {
            System.out.println(connectionManager.isConnected());
            while(connectionManager.isConnected()) {
                while(!toWrite.isEmpty()) {
                    connectionManager.writeString(toWrite.poll());
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        tWrite.start();
        return true;
    }

    public void setReadingCallback(ReadingRequests readCallback) {
        this.readCallback = readCallback;
    }

    private void tryProcessReading() {
        if (readCallback != null) {
            while(!toRead.isEmpty()) {
                String line = toRead.poll();
                System.out.println(line);
                readCallback.process(line);
            }
        }
    }

    public void resetReadingCallback() {
        readCallback = null;
    }
    public void sendRequest(String request) {
        toWrite.add(request);
    }
}
