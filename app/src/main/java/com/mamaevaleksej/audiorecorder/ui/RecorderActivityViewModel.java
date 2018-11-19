package com.mamaevaleksej.audiorecorder.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.mamaevaleksej.audiorecorder.Utils.AppRepository;
import com.mamaevaleksej.audiorecorder.model.Record;

import java.util.List;

public class RecorderActivityViewModel extends AndroidViewModel {

    private static final String TAG = RecorderActivityViewModel.class.getSimpleName();
    private LiveData<List<Record>> mRecordsList;
    private boolean isRecording;
    private int itemID = 0; // current record id, stored in the Room db;
    public final MutableLiveData<Record> record = new MutableLiveData<>();

    public RecorderActivityViewModel(@NonNull Application application) {
        super(application);
        AppRepository repository = AppRepository.getsInstance(application);
        mRecordsList = repository.getAllRecordsList();
    }

    public LiveData<List<Record>> getRecords(){
        return mRecordsList;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public LiveData<Record> getRecord(){
        return record;
    }

    public void setRecord(Record record){
        this.record.setValue(record);
    }
}
