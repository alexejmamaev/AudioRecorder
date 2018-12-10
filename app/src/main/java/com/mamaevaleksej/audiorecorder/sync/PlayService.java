package com.mamaevaleksej.audiorecorder.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

public class PlayService extends IntentService {

    private static final String TAG = PlayService.class.getSimpleName();
    public static final String ID = "current_record_id";
    public static final String ACTION_TRACK_IS_PLAYING = "track_is_playing";
    public static final String ACTION_TRACK_STOPPED_PLAYING = "track_stopped_playing";
    public static final String ACTION_FILE_NOT_FOUND = "file_not_found";

    private int mRecordId;

    public PlayService() {
        super("PlayService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            mRecordId = intent.getIntExtra(ID, 0);
        }
        /* Play record with current ID */
        AudioTrackPlayer.reversePlayRecordedAudioFile(this.getApplicationContext(), mRecordId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Play Service onDestoy called >>>>>>>>>>>");
    }
}
