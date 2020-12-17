package com.zybooks.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //declare variables
    //record buttons
    Button buttonRecord, buttonStopRecording;
    //playback buttons
    Button buttonPlayback, buttonStopPlayback;
    //where the recording will be saved
    String pathSave = "";

    //media player
    MediaPlayer myMediaPlayer;
    MediaRecorder myMediaRecorder;

    final int REQUEST_PERMISSION_CODE = 1000;
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    /*private ListView listView;
    private String recordingsNames[];*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


        //From Android M, you need to request Run-time permission

             buttonRecord.setOnClickListener(new View.OnClickListener() {

                 @Override
                 public void onClick(View view) {


                     if (checkPermissionFromDevice()) {

                         pathSave = getExternalCacheDir().getAbsolutePath()
                                  + "/" + UUID.randomUUID().toString() + "_audio_record.3gp";
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

                             myMediaPlayer.setDataSource(pathSave);
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

    }


    private void setupMediaRecorder() {
        myMediaRecorder = new MediaRecorder();
        myMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myMediaRecorder.setOutputFile(pathSave);
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