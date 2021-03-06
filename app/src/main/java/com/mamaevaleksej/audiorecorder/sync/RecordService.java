package com.mamaevaleksej.audiorecorder.sync;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mamaevaleksej.audiorecorder.Constants;
import com.mamaevaleksej.audiorecorder.Utils.InjectorUtils;

public class RecordService extends Service {

    private final String TAG = RecordService.class.getSimpleName();
    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    public static final String ACTION_RECORD = "com.mamaevaleksej.audiorecorder.sync.RecordService";
    private AudioRecorder mRecorder;


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
        mRecorder = InjectorUtils.provideAudioRecorder(this.getApplicationContext());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        boolean isRecording = intent.getBooleanExtra(Constants.IS_RECORDING, false);

        if (isRecording){
            NotificationTask.executeTask(this, NotificationTask.ACTION_SHOW_NOTIFICATION);
            final Runnable mRecordRunnable = () -> {
                mRecorder.recordAudioFile();
                // Stops recording in 10 second period
                mServiceHandler.postDelayed(() -> mRecorder
                        .stopRecordAudioFile(RecordService.this.getApplicationContext()), 10000);
            };
            mServiceHandler.post(mRecordRunnable);
        } else {
            mRecorder.stopRecordAudioFile(RecordService.this.getApplicationContext());
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
        Log.d(TAG, "onDestroy RecorderService =================== >>>>>>");
        mHandlerThread.quit();
    }
}
