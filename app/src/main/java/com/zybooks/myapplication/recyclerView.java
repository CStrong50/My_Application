package com.zybooks.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class recyclerView extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private String recordingsNames[];

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        ArrayList<File> recordings = readRecording(Environment.getExternalStorageDirectory());
        recordingsNames = new String[recordings.size()];
        for (int i = 0; i < recordings.size(); ++i){
            // do this so the song will only have the name in the list.
            recordingsNames[i] = recordings.get(i).getName().toString().replace(".mp3", "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.recorder_layout, R.id.textView, recordingsNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView< ? > parent, View view, int i, long l) {
                startActivity(new Intent(MainActivity.this, recyclerView.class)
                        .putExtra("position" , i).putExtra("list", recordings));
            }
        });
    }
    private ArrayList<File> readRecordings (File root){
        ArrayList<File> arrayList = new ArrayList<File>();
        File files[] = root.listFiles();

        for (File file: files){
            //check the files
            if (file.isDirectory()){
                arrayList.addAll(readRecording(file));
            }else {
                //if a file ends with .mp3
                if (file.getName().endsWith(" .mp3")){
                    //if file does end with .mp3 add to the file
                    arrayList.add(file);
                }
            }
        }
        return arrayList;
    }

    @Override
    public void onClick(View v) {

    }
}
