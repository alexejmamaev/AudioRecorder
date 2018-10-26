package com.mamaevaleksej.audiorecorder.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.mamaevaleksej.audiorecorder.Utils.AppRepository;
import com.mamaevaleksej.audiorecorder.model.Record;

import java.util.List;

public class RecorderActivityViewModel extends AndroidViewModel {

    private static final String TAG = RecorderActivityViewModel.class.getSimpleName();
    private LiveData<List<Record>> mRecordsList;
    private boolean isRecording;

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
}
