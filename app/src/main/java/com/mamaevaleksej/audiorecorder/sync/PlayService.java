package com.mamaevaleksej.audiorecorder.sync;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mamaevaleksej.audiorecorder.Utils.AppRepository;
import com.mamaevaleksej.audiorecorder.Utils.Constants;
import com.mamaevaleksej.audiorecorder.Utils.WavConverterUtils;

import java.io.File;
import java.io.IOException;

public class PlayService extends IntentService {

    private static final String TAG = PlayService.class.getSimpleName();
    public static final String ACTION_PLAY = "com.mamaevaleksej.audiorecorder.sync.PlayService";
    public static final String ID = "current_record_id";
    private MediaPlayer mMediaPlayer;

    private int mRecordId;

    private AudioTrack mAudioTrack;

    public PlayService() {
        super("PlayService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null){
            mRecordId = intent.getIntExtra(ID, 0);
        }
        reversePlayRecordedAudioFile();
    }

    // Kicks off straight play of the recorded file
    private void playRecordedAudioFile(){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(getLastRecordedFilePath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    // Kicks off reverse play of the recorded file
    private void reversePlayRecordedAudioFile() {

        // Checks if the file exists (file path is valid)
        final File file = new File(getLastRecordedFilePath());
        if (!file.exists()){
            // Send broadcast indicating that file path is invalid (file doesn't exist)
            LocalBroadcastManager mBroadcastManager = LocalBroadcastManager.getInstance(this);
            Intent noFileIntent = new Intent(ACTION_PLAY);
            mBroadcastManager.sendBroadcast(noFileIntent);
            stopSelf();
            return;
        }

            int minBufferSize = AudioTrack.getMinBufferSize(Constants.RECORDER_SAMPLERATE,
                    Constants.RECORDER_CHANNELS, Constants.RECORDER_AUDIO_ENCODING);

            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, Constants.RECORDER_SAMPLERATE,
                    Constants.RECORDER_CHANNELS, Constants.RECORDER_AUDIO_ENCODING, minBufferSize,
                    AudioTrack.MODE_STREAM);

                // Checks when audio track stop playing
            boolean playbackFinished = WavConverterUtils.playReverse(file, mAudioTrack);
                if (playbackFinished){
                    stopSelf();
                    }
    }

    // Returns the last recorded audio file by it's path
    private String getLastRecordedFilePath(){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
//                (getApplicationContext());
//        String mRecordedFilePath = preferences.getString(Constants.RECORDED_FILE_PATH,
//                Constants.RECORDED_FILE_PATH_IS_MISSING);
//        return (mRecordedFilePath.equals
//                (Constants.RECORDED_FILE_PATH_IS_MISSING)) ? "" : mRecordedFilePath;

        return AppRepository.getsInstance(this).getRecordFilePath(mRecordId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        if (mAudioTrack != null && mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED){
            mAudioTrack.stop();
            mAudioTrack.release();
        }
        Log.d(TAG, "Play Service onDestoy called >>>>>>>>>>>");
    }
}
