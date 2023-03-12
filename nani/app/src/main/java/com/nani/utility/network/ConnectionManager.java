package com.nani.utility.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionManager {
    private final String serverIP;
    private final int serverPort;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private boolean isConnected = false;

    public ConnectionManager() {
        this("51.142.153.253", 10130);
    } //"51.142.153.253"localhost
    public ConnectionManager(String IP, int PORT) {
        this.serverIP = IP;
        this.serverPort = PORT;
    }

    public boolean connect() {
        final boolean[] ret = {false};
        Thread temp = new Thread(() -> {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIP);
                socket = new Socket(serverAddr, serverPort);
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ret[0] = true;
            } catch (IOException e) {
                e.printStackTrace();
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                ret[0] = false;
            }
            ret[0] = true;
        });
        temp.start();
        try {
            temp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            ret[0] = false;
        }
        isConnected = ret[0];
        return ret[0];
    }

    public String readLine() {
        if (reader != null) {
            try {
                return reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public boolean disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        isConnected = false;
        return true;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void writeString(String out) {
        writer.println(out);
    }

}
