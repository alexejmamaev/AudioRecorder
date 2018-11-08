package com.mamaevaleksej.audiorecorder.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mamaevaleksej.audiorecorder.Utils.AudioTrackPlayer;

public class PlayService extends IntentService {

    private static final String TAG = PlayService.class.getSimpleName();
    public static final String ACTION_PLAY = "com.mamaevaleksej.audiorecorder.sync.PlayService";
    public static final String ID = "current_record_id";

    private int mRecordId;

    public PlayService() {
        super("PlayService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            mRecordId = intent.getIntExtra(ID, 0);
        }
        AudioTrackPlayer.reversePlayRecordedAudioFile(this, mRecordId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioTrackPlayer.stopPlaying();
        Log.d(TAG, "Play Service onDestoy called >>>>>>>>>>>");
    }
}
