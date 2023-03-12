package com.example.zadb;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    File path = null;


    private void loadAddresses() {
        try {
            String[] allUrls = Utility.readFrom(path, "allNames").split("\n");
            for (String url : allUrls) {
                if (!url.equals(""))
                    urls.add(Utility.getGoodUrl(url));
            }
        } catch(IOException ignored) {
            File f = new File(path, "allNames");
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void tryUpdateOnUrl(String url) {
        try {
            String content = Utility.loadUrlContent(url);
            Utility.writeTo(path, url, content);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    List<String> urls;

    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //path = getApplicationContext().getFilesDir();
        path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        Toast.makeText(this, "Path to download files:" + path.toString(), Toast.LENGTH_LONG).show();
        urls = new ArrayList<>();
        loadAddresses();
        setContentView(R.layout.main_activity);


        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_view_layout, urls);
        listView = findViewById(R.id.id_list_view);
        listView.setAdapter(arrayAdapter);
        editText = findViewById(R.id.id_edit_text);

        Intent intent = new Intent(this, HelloService.class);
        intent.putExtra("path_name", path.toString());
        startService(intent);
    }

    public void addItemToList(View view) {
        if (editText.getText().toString().equals(""))
            return;

        Intent intent = new Intent(this, HelloService.class);
        stopService(intent);
        try {
            String newUrl = editText.getText().toString();
            if (!Utility.checkUrl(newUrl)) {
                Toast.makeText(this, "Incorrect url!!!", Toast.LENGTH_SHORT).show();
            } else {
                String goodUrl = Utility.getGoodUrl(newUrl);
                String folderUrl = Utility.getFoldUrl(newUrl);
                new FileOutputStream(new File(path, folderUrl));
                Utility.addTo(path, "allNames", newUrl);
                urls.add(goodUrl);
                arrayAdapter.notifyDataSetChanged();
                editText.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent2 = new Intent(this, HelloService.class);
        intent2.putExtra("path_name", path.toString());
        startService(intent2);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}