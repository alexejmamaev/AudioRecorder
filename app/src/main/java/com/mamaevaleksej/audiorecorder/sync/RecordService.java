package com.mamaevaleksej.audiorecorder.sync;

import android.app.Service;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Chronometer;

import com.mamaevaleksej.audiorecorder.Utils.AppExecutors;
import com.mamaevaleksej.audiorecorder.Utils.AppRepository;
import com.mamaevaleksej.audiorecorder.Utils.Constants;
import com.mamaevaleksej.audiorecorder.Utils.WavConverterUtils;
import com.mamaevaleksej.audiorecorder.model.Record;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class RecordService extends Service {

    private final String TAG = RecordService.class.getSimpleName();

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;

    private LocalBroadcastManager mBroadcastManager;
    public static final String ACTION_RECORD = "com.mamaevaleksej.audiorecorder.sync.RecordService";

    private Chronometer mChronometer;

    private AudioRecord mRecorder = null;
    private String mRecordedFilePath, mTempFilePath;
    private boolean isRecording;


    private final class ServiceHandler extends Handler{

        // Define how the handler will process messages
        public ServiceHandler(Looper looper){
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message msg) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandlerThread = new HandlerThread("RecordService.HandlerThread");
        mHandlerThread.start();
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());
        mBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        isRecording = intent.getBooleanExtra(Constants.IS_RECORDING, false);

        if (isRecording){
            NotificationTask.executeTask(getApplicationContext(), Constants.ACTION_SHOW_NOTIFICATION);
            final Runnable mRecordRunnable = new Runnable() {
                @Override
                public void run() {
                    recordAudioFile();
                    Log.d(TAG, "CHRONOMETER STARTS ======== >");
                    // Stops recording in 10 second period
                    mServiceHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopRecordAudioFile();
                        }
                    }, 10000);
                }
            };
            mServiceHandler.post(mRecordRunnable);
        } else {
            stopRecordAudioFile();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mHandlerThread.quit();
    }

    private void recordAudioFile() {
        mChronometer = new Chronometer(getApplicationContext());
        mChronometer.start();
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                Constants.RECORDER_SAMPLERATE, Constants.RECORDER_CHANNELS,
                Constants.RECORDER_AUDIO_ENCODING, Constants.BUFFER_SIZE);

        int i = mRecorder.getState();
        if (i == 1) mRecorder.startRecording();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        });


    }

    private void writeAudioDataToFile() {

        byte[] data = new byte[Constants.BUFFER_SIZE];
        FileOutputStream os = null;
        mTempFilePath = WavConverterUtils.getTempFilename();
        try {
            os = new FileOutputStream(mTempFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int read = 0;

        if (null != os) {
            while (isRecording) {
                read = mRecorder.read(data, 0, Constants.BUFFER_SIZE);

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopRecordAudioFile() {
        isRecording = false;
        NotificationTask.executeTask(getApplicationContext(), Constants.ACTION_CLEAR_NOTIFICATION);
        if (null != mRecorder) {
            int i = mRecorder.getState();
            if (i == 1) mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            if (mChronometer != null){
                mChronometer.stop();
            }
        }
        mRecordedFilePath = WavConverterUtils.getFilePath();
        WavConverterUtils.copyWaveFile(WavConverterUtils.getTempFilename(), mRecordedFilePath, Constants.BUFFER_SIZE);
        WavConverterUtils.deleteTempFile(WavConverterUtils.getTempFilename());

        java.util.Date date = Calendar.getInstance().getTime();

        long recordLengthInMlls = (SystemClock.elapsedRealtime() - mChronometer.getBase());

        Intent intent1 = new Intent(ACTION_RECORD);
        intent1.putExtra(Constants.RECORDED_FILE_PATH, mRecordedFilePath);
        mBroadcastManager.sendBroadcast(intent1);

//         Put recorded file path to preferences
//        SharedPreferences preferences = PreferenceManager
//                .getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(Constants.RECORDED_FILE_PATH, mRecordedFilePath);
//        editor.apply();

        Record mRecord = new Record(mRecordedFilePath, date, recordLengthInMlls);

        AppRepository.getsInstance(this).insertNewRecord(mRecord);

            stopSelf();
    }
}
