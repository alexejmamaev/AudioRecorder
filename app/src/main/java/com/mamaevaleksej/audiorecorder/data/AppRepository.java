package com.mamaevaleksej.audiorecorder.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.mamaevaleksej.audiorecorder.AppExecutors;

import java.util.List;

public class AppRepository {

    private static final String TAG = AppRepository.class.getSimpleName();
    private static AppRepository sInstance;
    private static final Object LOCK = new Object();
    private RecordsDAO mDAO;
    private AppExecutors mExecutors;

    private AppRepository(RecordsDAO dao, AppExecutors executors) {
        mDAO = dao;
        mExecutors = executors;
    }

    // Синглтон репозитария
    public synchronized static AppRepository getsInstance(RecordsDAO dao, AppExecutors executors){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = new AppRepository(dao, executors);
            }
        }
        return sInstance;
    }

    public void insertNewRecord(final Record record){
        Log.d(TAG, "Repository: inserting new record at ------->" + record.getFilePath());
        mExecutors.diskIO().execute(() -> mDAO.insertRecord(record));
    }

    public LiveData<List<Record>> getAllRecordsList(){
        Log.d(TAG, "Getting all records list via repository !!!!!!!!!!!!!!!!");
        return mDAO.loadAllRecords();
    }

    public Record getRecord(final int id){
        Log.d(TAG, "Getting record by id: " + id + " !!!!!!!!!!!!!!!!!!");
        return mDAO.getRecordById(id);
    }

    public void deleteRecord(final int id){
        Log.d(TAG, "Deleting record id " + id + " !!!!!!!!!!!!!");
        mExecutors.diskIO().execute(() -> mDAO.deleteRecord(id));
    }

    public void deleteAllRecords(){
        mDAO.nukeTable();
    }

    public void setToListened(final int id){
        mExecutors.diskIO().execute(() -> mDAO.setListened(id));
    }

    public List getAllIgnoredRecords(){
         return mDAO.getAllIgnoredRecordsList();
    }

}
