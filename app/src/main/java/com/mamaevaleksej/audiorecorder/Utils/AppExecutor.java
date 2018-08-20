package com.mamaevaleksej.audiorecorder.Utils;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {
    public static final String TAG = AppExecutor.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static AppExecutor sInstance;
    private final Executor ioExecutor;


    private AppExecutor(Executor ioExecutor) {
        this.ioExecutor = ioExecutor;
    }

    public static AppExecutor getInstance(){
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = new AppExecutor(Executors.newFixedThreadPool(1));
                Log.d(TAG, "Create a new sInstance *************");
            };
        }
        Log.d(TAG, "Returning sInstance *************");
        return sInstance;
    }

    public Executor IoExecutor() {
        return ioExecutor;
    }
}
