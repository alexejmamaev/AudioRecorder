package com.mamaevaleksej.audiorecorder.Utils;

import android.app.ActivityManager;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import com.mamaevaleksej.audiorecorder.data.AppDatabase;
import com.mamaevaleksej.audiorecorder.model.Record;

import java.util.List;

public class AppRepository {

    private static final String TAG = AppRepository.class.getSimpleName();
    private static AppRepository sInstance;
    private static final Object LOCK = new Object();
    private AppDatabase mDb;
    private List<Record> mRecordsList;

    private AppRepository(Context context) {
        this.mDb = AppDatabase.getsInstance(context);
    }

    // Синглтон репозитария
    public synchronized static AppRepository getsInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = new AppRepository(context);
            }
        }
        return sInstance;
    }

    public void insertNewRecord(final Record record){
        Log.d(TAG, "Repository: inserting new record at ------->" + record.getFilePath());
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.Dao().insertRecord(record);
            }
        });
    }

    public LiveData<List<Record>> getAllRecordsList(){
        Log.d(TAG, "Getting all records list via repository !!!!!!!!!!!!!!!!");
        return mDb.Dao().loadAllRecords();
    }

    public String getRecordFilePath(final int id){
        Log.d(TAG, "Getting record by id: " + id + " !!!!!!!!!!!!!!!!!!");
        return   mDb.Dao().getRecordById(id).getFilePath();
    }

    public void deleteRecord(final int id){
        Log.d(TAG, "Deleting record id " + id + " !!!!!!!!!!!!!");
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.Dao().deleteRecord(id);
            }
        });
    }

    public void deleteAllRecords(){
        mDb.clearAllTables();
    }

    // Helper method to check if service is running
    public boolean myServiceIsRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
