package com.mamaevaleksej.audiorecorder.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.mamaevaleksej.audiorecorder.model.Record;
import com.mamaevaleksej.audiorecorder.sync.PlayService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AudioTrackPlayer {

    private static final String TAG = AudioTrackPlayer.class.getSimpleName();
    private static MediaPlayer mMediaPlayer;
    private static AudioTrack mAudioTrack;
    private static FileInputStream is;
    private static File file;

    // Kicks off straight play of the recorded file from the beginning if the record
    public void playRecordedAudioFile(Context context, int id){
        if (notValidFilePath(context, id)) {
            context.stopService(new Intent(context, PlayService.class));
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getLastRecordedFilePath(context, id));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Kicks off reverse play of the recorded file
    public static void reversePlayRecordedAudioFile(Context context, int id) {
        if (notValidFilePath(context, id)) {
            context.stopService(new Intent(context, PlayService.class));
            return;
        }

        if (mAudioTrack != null && mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED){
            stopPlaying();
        }

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, Constants.RECORDER_SAMPLERATE,
                Constants.RECORDER_CHANNELS, Constants.RECORDER_AUDIO_ENCODING,
                AudioTrack.getMinBufferSize(Constants.RECORDER_SAMPLERATE,
                        Constants.RECORDER_CHANNELS, Constants.RECORDER_AUDIO_ENCODING),
                AudioTrack.MODE_STREAM);

            // Checks when audio track stop playing
            boolean playbackFinished = AudioTrackPlayer.playReverse();

            if (playbackFinished){
                if (AppRepository.getsInstance(context).myServiceIsRunning(context, PlayService.class)){
                    context.stopService(new Intent(context, PlayService.class));
                }
            }
    }

    public static void stopPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        if (mAudioTrack != null && mAudioTrack.getState() == AudioTrack.STATE_INITIALIZED){
            mAudioTrack.pause();
            mAudioTrack.flush();
        }
    }

    // Returns the last recorded audio file by it's path
    private static String getLastRecordedFilePath(Context context, int id){
        Record record = AppRepository.getsInstance(context).getRecordFilePath(id);
        if (record == null) {
            return null;
        }
        return record.getFilePath();
    }

    private static boolean playReverse() {
        mAudioTrack.play();
        byte[] buffer = new byte[Constants.BUFFER_ELEMENTS];

        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            buffer = convertStreamToByteArray(is, Constants.BUFFER_ELEMENTS);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        mAudioTrack.write(buffer, 0, buffer.length);
        boolean playbackFinished = false;

        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (buffer.length != 0){
            playbackFinished = true;
        }

        return playbackFinished;
    }

    private static byte[] convertStreamToByteArray(InputStream is, int size) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[size];
//        int i = Integer.MAX_VALUE;
        int i;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }
        return reverse(baos.toByteArray());
    }

    private static byte[] reverse(byte[] array) {
        if (array == null) return null;
        byte[] result = new byte[array.length];
        for (int i = 0; i < 44; i++) {
            result[i] = array[i];
        }

        int o = array.length - 1;

        for (int l = 45; l < array.length; l++) {
            byte value1 = array[l]; //first value is array[44];
            result[o] = value1; // last value for result will be the first one from the array
            o--;
        }
        return result;
    }

    private static void sendFileNotExistMessage(Context context){
        // Send broadcast indicating that file path is invalid (file doesn't exist)
        LocalBroadcastManager mBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent noFileIntent = new Intent(PlayService.ACTION_PLAY);
        mBroadcastManager.sendBroadcast(noFileIntent);
        context.stopService(new Intent(context, PlayService.class));
    }

    // Checks whether the file exists OR
    private static boolean notValidFilePath(Context context, int id){
        // Checks if the DB contains file path
        String filePath = getLastRecordedFilePath(context, id);
        if (TextUtils.isEmpty(filePath)){
            sendFileNotExistMessage(context);
            return true;
        }

        if (!TextUtils.isEmpty(filePath)){
            file = new File(filePath);
            // Checks if the file exists (file path is valid)
            if (!file.exists()){
                sendFileNotExistMessage(context);
                return true;
            }
        }
        return false;
    }

}