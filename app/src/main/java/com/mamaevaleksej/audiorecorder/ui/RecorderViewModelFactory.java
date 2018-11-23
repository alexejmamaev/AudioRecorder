package com.mamaevaleksej.audiorecorder.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.mamaevaleksej.audiorecorder.data.AppRepository;

public class RecorderViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppRepository repository;

    public RecorderViewModelFactory(AppRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RecorderActivityViewModel(repository);
    }
}
