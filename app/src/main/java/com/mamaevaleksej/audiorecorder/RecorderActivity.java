package com.mamaevaleksej.audiorecorder;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mamaevaleksej.audiorecorder.Utils.Constants;

public class RecorderActivity extends AppCompatActivity {

    private final String TAG = RecorderActivity.class.getSimpleName();
//    public static final String ACTION = "com.mamaevaleksej.audiorecorder.RecorderActivity";
    private Toast mToast;
    private Button mButtonSave, mButtonPlay;
    private boolean isRecording = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // Setup callbacks from Services via BroadcastReceiver
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Set up actions upon callback from Record service
            if (intent.getAction().equals(RecordService.ACTION_RECORD)){
                String mRecordedFilePath = intent.getStringExtra(Constants.RECORDED_FILE_PATH);
                isRecording = false;
                mButtonSave.setText(R.string.button_save);
                if (mToast != null) mToast.cancel();
                String toastMssg = String.format(getString(R.string.recording_finished), mRecordedFilePath);
                mToast = Toast.makeText(RecorderActivity.this, toastMssg, Toast.LENGTH_SHORT);
                mToast.show();
            }

            // Set up actions upon callback from Play service
            if (intent.getAction().equals(PlayService.ACTION_PLAY)){
                // Kicks off file not found toast
                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText
                        (RecorderActivity.this, R.string.file_not_found, Toast.LENGTH_SHORT);
                mToast.show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        initViews();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.IS_RECORDING)){
                isRecording = savedInstanceState.getBoolean(Constants.IS_RECORDING);
                if (isRecording){
                    mButtonSave.setText(R.string.button_stop);
                } else {
                    mButtonSave.setText(R.string.button_save);
                }
            }
        }

        // Register BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(RecordService.ACTION_RECORD);
        filter.addAction(PlayService.ACTION_PLAY);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        Log.d(TAG, "On create Service Record is running: -------> " +
                myServiceIsRunning(RecordService.class));
        Log.d(TAG, "On create Service Play is running: -------> " +
                myServiceIsRunning(PlayService.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.IS_RECORDING, isRecording);
        super.onSaveInstanceState(outState);
    }

    //    Initialize this Activity views
    private void initViews() {

        /* Set save button */
        mButtonSave = findViewById(R.id.buttonRecord);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supportMicCheck()) {

                    if (!permissionsGranted()) {
                        ActivityCompat.requestPermissions(RecorderActivity.this, permissions,
                                Constants.REQUEST_PERMISSIONS);
                    } else {
                        // Kicks off new Record service if one hasn't been started before
                        if (!isRecording) {
                            isRecording = true;
                            mButtonSave.setText(R.string.button_stop);
                            setRecordService();
                        } else {
                            // Passes negative flag to the running Record service in order to stop it
                            mButtonSave.setText(R.string.button_save);
                            isRecording = false;
                            setRecordService();
//                            Intent stopRecordingIntent = new Intent(ACTION);
//                            stopRecordingIntent.putExtra(Constants.IS_RECORDING, isRecording);
//                            LocalBroadcastManager.getInstance(RecorderActivity.this)
//                                    .sendBroadcast(stopRecordingIntent);
//                            Log.d(TAG, "Sending broadcast from Activity **********");
                        }
                    }

                } else {
                    if (mToast != null){
                        mToast.cancel();
                    }
                    mToast = Toast.makeText(RecorderActivity.this, getString(R.string.no_mic),
                            Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        });

        /* Set play button */
        mButtonPlay = findViewById(R.id.buttonPlay);
        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Kicks off new Play service if Play service isn't running yet
                if (!myServiceIsRunning(PlayService.class)) setPlayService();
                else Log.d(TAG, "on Button click check: Service Play is running: -------> " +
                        myServiceIsRunning(PlayService.class));
                }
        });
    }

    //    Checks whether the device has microphone or not
    private boolean supportMicCheck() {
        PackageManager manager = this.getPackageManager();
        return manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    // Checks if all the permissions have been granted
    private boolean permissionsGranted() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void setRecordService(){
        Intent intent = new Intent(this, RecordService.class);
        intent.putExtra(Constants.IS_RECORDING, isRecording);
        startService(intent);
        Log.d(TAG, "setRecordService Service Record is running: -------> " +
                myServiceIsRunning(RecordService.class));
    }

    private void setPlayService(){
        Intent playServiceIntent = new Intent(this, PlayService.class);
        startService(playServiceIntent);
        Log.d(TAG, "setPlayService Service Play is running: -------> " +
                myServiceIsRunning(PlayService.class));
    }

    // Helper method to check if service is running
    private boolean myServiceIsRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Called if the app is killed
        if (isFinishing()){
            if (mToast != null){
                mToast.cancel();
            }
            Log.d(TAG, "App is killed >>>>>>>>>>>");
            // Stops Record Service
           if (myServiceIsRunning(RecordService.class)){
               isRecording = false;
               setRecordService();
           }
            // Stops Play Service
            if (myServiceIsRunning(PlayService.class)){
                stopService(new Intent(this, PlayService.class));
            }
            // Unregister BroadcastReceiver
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }

        // Called when activity is recreated due to orientation change (app is not killed)
        else {
            Log.d(TAG, "Orientation change >>>>>>>>>>>>");
        }
    }

}
