package com.mamaevaleksej.audiorecorder.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.mamaevaleksej.audiorecorder.data.AppRepository;
import com.mamaevaleksej.audiorecorder.data.Record;

import java.util.List;

public class RecorderActivityViewModel extends ViewModel {

    private static final String TAG = RecorderActivityViewModel.class.getSimpleName();
    private LiveData<List<Record>> mRecordsList;
    private boolean isRecording;
    private int itemID = 0; // current record id, stored in the Room db;
    private int listSize; // size of the list to check if it
    private final MutableLiveData<Record> record = new MutableLiveData<>();

    public RecorderActivityViewModel(AppRepository repository) {
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

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }
}
