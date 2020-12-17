package com.zybooks.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //declare variables
    //record buttons
    Button buttonRecord, buttonStopRecording;
    //playback buttons
    Button buttonPlayback, buttonStopPlayback;
    Button buttonDelete;

    //where the recording will be saved
    //String pathSave = "";
    private DateFormat dateFormatter;

    //media player
    MediaPlayer myMediaPlayer;
    MediaRecorder myMediaRecorder;

    final int REQUEST_PERMISSION_CODE = 1000;
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    private Spinner spinnerAudio = null;
    ArrayAdapter<String> adapter;
    ArrayList<String> filesNames;

    private String fileName;
    private File currentOutputFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //formats date
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss" , Locale.US);
        //find view spinner so we can work with it
        spinnerAudio = findViewById(R.id.spinner1);

        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath());
        //make sure to do file directory and not fileName
        //returns arraylist not objects
        File[] fileLists= file.listFiles();

        //create array list

        filesNames = new ArrayList<>();

        //wants just the file not the file names
        assert fileLists != null;
        if (fileLists.length != 0){
            for (File name : fileLists){
                filesNames.add(name.getName());
            }
        }

        //this is what gets pasted in
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filesNames);

        //creates dropdown menu
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAudio.setAdapter(adapter);
        spinnerAudio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView< ? > parent, View view, int position, long id) {
                //get name of the file in that position
                fileName = parent.getItemAtPosition(position).toString();
                //might cause an error
                currentOutputFile = new File(getApplicationContext().getFilesDir(), fileName);
            }
        });


        //request RunTime permission
        if (checkPermissionFromDevice())
            requestPermission();

        //init View
        //btnRecord
        buttonRecord = (Button) findViewById(R.id.buttonStartRecord);
        //btnStopRecord
        buttonStopRecording = (Button) findViewById(R.id.buttonStopRecord);
        //btnPlay
        buttonPlayback = (Button) findViewById(R.id.buttonPlaybackStart);
        //btnStop
        buttonStopPlayback =(Button) findViewById(R.id.buttonStopPlayback);
        //button  delete
        buttonDelete = (Button) findViewById(R.id.button_delete);


        //From Android M, you need to request Run-time permission

             buttonRecord.setOnClickListener(new View.OnClickListener() {

                 @Override
                 public void onClick(View view) {


                     if (checkPermissionFromDevice()) {


                         fileName = getExternalCacheDir().getAbsolutePath()
                                  + dateFormatter.format(Calendar.getInstance().getTime())+
                                 ".3gp";
                         setupMediaRecorder();
                         try {
                             myMediaRecorder.prepare();
                             myMediaRecorder.start();

                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         //btnplay
                         buttonPlayback.setEnabled(false);
                         //btnStop
                         buttonStopPlayback.setEnabled(false);
                         //stop recording
                         buttonStopRecording.setEnabled(true);
                         Toast.makeText(MainActivity.this,
                                 "Recording.....", Toast.LENGTH_SHORT).show();

                     } else {
                         requestPermission();
                     }
                 }

             });
             buttonStopRecording.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     myMediaRecorder.stop();
                     buttonStopRecording.setEnabled(false);
                     buttonPlayback.setEnabled(true);
                     buttonRecord.setEnabled(true);
                     buttonStopPlayback.setEnabled(false);
                 }
             });
             buttonPlayback.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     buttonStopPlayback.setEnabled(true);
                     buttonStopRecording.setEnabled(false);
                     buttonRecord.setEnabled(false);

                     myMediaPlayer = new MediaPlayer();
                     try {

                             myMediaPlayer.setDataSource(fileName);
                             myMediaPlayer.prepare();

                     }catch (IOException e){

                         e.printStackTrace();
                     }
                     myMediaPlayer.start();
                     Toast.makeText(MainActivity.this,
                             "Playing Recording.....", Toast.LENGTH_SHORT).show();
                 }
             });
             buttonStopPlayback.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     buttonStopRecording.setEnabled(false);
                     buttonRecord.setEnabled(true);
                     buttonStopPlayback.setEnabled(false);
                     buttonPlayback.setEnabled(true);

                     if (myMediaPlayer != null){
                         myMediaPlayer.stop();
                         myMediaPlayer.release();
                         setupMediaRecorder();
                     }
                 }
             });

             //delete button
             buttonDelete.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                 }
             });

    }

    private void setupMediaRecorder() {
        //fileName = getCacheDir()

        myMediaRecorder = new MediaRecorder();
        myMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myMediaRecorder.setOutputFile(fileName);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO

        } , REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length >0 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission is granted",
                            Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission is Denied", Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;

    }

}