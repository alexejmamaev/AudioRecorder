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

import com.mamaevaleksej.audiorecorder.Utils.AudioRecorder;
import com.mamaevaleksej.audiorecorder.Utils.Constants;

public class RecordService extends Service {

    private final String TAG = RecordService.class.getSimpleName();
    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    public static final String ACTION_RECORD = "com.mamaevaleksej.audiorecorder.sync.RecordService";

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
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        boolean isRecording = intent.getBooleanExtra(Constants.IS_RECORDING, false);

        if (isRecording){
            NotificationTask.executeTask(this, Constants.ACTION_SHOW_NOTIFICATION);
            final Runnable mRecordRunnable = new Runnable() {
                @Override
                public void run() {
                    AudioRecorder.getsInstance().recordAudioFile(System.currentTimeMillis());
                    // Stops recording in 10 second period
                    mServiceHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AudioRecorder.getsInstance()
                                    .stopRecordAudioFile(RecordService.this);
                        }
                    }, 10000);
                }
            };
            mServiceHandler.post(mRecordRunnable);
        } else {
            AudioRecorder.getsInstance()
                    .stopRecordAudioFile(RecordService.this);
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
