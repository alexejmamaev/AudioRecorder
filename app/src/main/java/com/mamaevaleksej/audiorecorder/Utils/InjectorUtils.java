package com.mamaevaleksej.audiorecorder.Utils;

import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.mamaevaleksej.audiorecorder.AppExecutors;
import com.mamaevaleksej.audiorecorder.Constants;
import com.mamaevaleksej.audiorecorder.data.AppDatabase;
import com.mamaevaleksej.audiorecorder.data.AppRepository;
import com.mamaevaleksej.audiorecorder.sync.AudioRecorder;
import com.mamaevaleksej.audiorecorder.ui.RecorderViewModelFactory;

/**
 * Provides static methods to inject the various classes needed for the app
 */
public class InjectorUtils {

    public static AppRepository provideRepository(Context context){
        AppDatabase database = AppDatabase.getsInstance(context);
        AppExecutors executors = AppExecutors.getInstance();
        return AppRepository.getsInstance(database.Dao(), executors);
    }

    public static RecorderViewModelFactory provideViewModelFactory(Context context){
        AppRepository repository = provideRepository(context.getApplicationContext());
        return new RecorderViewModelFactory(repository);
    }

    public static AudioRecorder provideAudioRecorder(Context context){
        AppRepository repository = provideRepository(context);
        AppExecutors executors = AppExecutors.getInstance();
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                Constants.RECORDER_SAMPLERATE, Constants.RECORDER_CHANNELS,
                Constants.RECORDER_AUDIO_ENCODING, Constants.BUFFER_SIZE);
        return AudioRecorder.getsInstance(executors, repository, audioRecord);
    }

}
