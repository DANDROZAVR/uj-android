package com.example.zadb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class Utility {
    private static String readAllFromStream(BufferedReader in) throws IOException {
        String inputLine;
        StringBuilder res = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            res.append(inputLine).append("\n");
        in.close();
        return res.toString();
    }
    public static String loadUrlContent(String url) throws IOException {
        URL yahoo = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yahoo.openStream()));
        String res = readAllFromStream(in);
        in.close();
        return res;
    }
    public static boolean checkUrl(String url) throws IOException {
       /* try {
            URL yahoo = new URL(url);
            HttpURLConnection huc;
            huc = (HttpURLConnection) yahoo.openConnection();
            int responseCode = huc.getResponseCode();
            return HttpURLConnection.HTTP_OK == responseCode;

        } catch (Exception e) {
            e.printStackTrace();;
            return false;
        }*/
    }
    public static String readFrom(File path, String name) throws IOException {
        File file = new File(path, name);
        BufferedReader in = new BufferedReader(
                new FileReader(
                        file));
        String res = readAllFromStream(in);
        in.close();
        return res;
    }
    public static void writeTo(File path, String name, String content) throws IOException {
        File file = new File(path, name);
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }
    public static void writeTo(String path, String name, String content) throws IOException {
        File file = new File(path, name);
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }
    public static void addTo(File path, String name, String content) throws IOException {
        writeTo(path, name, readFrom(path, name)  + content + "\n");
    }
    public static String getGoodUrl(String url) {
        return url.replace('^', '/');
    }
    public static String getFoldUrl(String url) {
        return url.replace('/', '^');
    }
}
