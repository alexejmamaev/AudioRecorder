package com.mamaevaleksej.audiorecorder;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.mamaevaleksej.audiorecorder.Utils.Constants;
import com.mamaevaleksej.audiorecorder.Utils.WavConverterUtils;

import java.io.File;
import java.io.IOException;

public class PlayService extends IntentService {

    private static final String TAG = PlayService.class.getSimpleName();
    private boolean isPlaying = false;

    private LocalBroadcastManager mBroadcastManager;
    public static final String ACTION_PLAY = "com.mamaevaleksej.audiorecorder.PlayService";

    public PlayService() {
        super("PlayService");
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     *               This may be null if the service is being restarted after
     *               its process has gone away; see
     *               {@link Service#onStartCommand}
     *               for details.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        reversePlayRecordedAudioFile();
    }

    // Kicks off straight play of the recorded file
    private void playRecordedAudioFile(){
        if (!isPlaying){
            isPlaying = true;
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(getLastRecordedFilePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Kicks off reverse play of the recorded file
    private void reversePlayRecordedAudioFile() {

        // Checks if the file exists (file path is valid)
        final File file = new File(getLastRecordedFilePath());
        if (!file.exists()){
            isPlaying = false;
            // Send broadcast indicating that file path is invalid (file doesn't exist)
            mBroadcastManager = LocalBroadcastManager.getInstance(this);
            Intent noFileIntent = new Intent(ACTION_PLAY);
            mBroadcastManager.sendBroadcast(noFileIntent);
            stopSelf();
            return;
        }

        if (!isPlaying) {
            isPlaying = true;

            int minBufferSize = AudioTrack.getMinBufferSize(Constants.RECORDER_SAMPLERATE,
                    Constants.RECORDER_CHANNELS, Constants.RECORDER_AUDIO_ENCODING);

            final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, Constants.RECORDER_SAMPLERATE,
                    Constants.RECORDER_CHANNELS, Constants.RECORDER_AUDIO_ENCODING, minBufferSize,
                    AudioTrack.MODE_STREAM);

            while (isPlaying) {
                // Checks when audio track stop playing
                boolean playbackFinished = WavConverterUtils.playReverse(file, audioTrack);
                if (playbackFinished){
                    audioTrack.stop();
                    audioTrack.release();
                    isPlaying = false;
                }
            }
        }
    }

    // Returns the last recorded audio file by it's path
    private String getLastRecordedFilePath(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());
        String mRecordedFilePath =  preferences.getString(Constants.RECORDED_FILE_PATH,
                Constants.RECORDED_FILE_PATH_IS_MISSING);
        return (mRecordedFilePath.equals
                (Constants.RECORDED_FILE_PATH_IS_MISSING)) ? "" : mRecordedFilePath;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        Log.d(TAG, "Play Service onDestoy called >>>>>>>>>>>");
    }
}
