package com.mamaevaleksej.audiorecorder.Utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Chronometer;

import com.mamaevaleksej.audiorecorder.model.Record;
import com.mamaevaleksej.audiorecorder.sync.NotificationTask;
import com.mamaevaleksej.audiorecorder.sync.RecordService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AudioRecorder {

    private final static String TAG = AudioRecorder.class.getSimpleName();
    private static AudioRecorder sInstance;
    private static final Object LOCK = new Object();
    private AudioRecord mAudioRecord;
    private Chronometer mChronometer;
    private boolean isRecording;

    private AudioRecorder(){
        this.mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                Constants.RECORDER_SAMPLERATE, Constants.RECORDER_CHANNELS,
                Constants.RECORDER_AUDIO_ENCODING, Constants.BUFFER_SIZE);
    }

    public synchronized static AudioRecorder getsInstance(){
        if (sInstance == null){
            synchronized (LOCK){
                sInstance = new AudioRecorder();
            }
        }
        return sInstance;
    }

    public void recordAudioFile(Context context) {
        if (mAudioRecord != null){
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        isRecording = true;
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                Constants.RECORDER_SAMPLERATE, Constants.RECORDER_CHANNELS,
                Constants.RECORDER_AUDIO_ENCODING, Constants.BUFFER_SIZE);

        mChronometer = new Chronometer(context);
        mChronometer.start();

        int i = mAudioRecord.getState();
        if (i == 1) mAudioRecord.startRecording();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                writeAudioDataToFile();
            }
        });
    }

    private void writeAudioDataToFile() {
        byte[] data = new byte[Constants.BUFFER_SIZE];
        FileOutputStream os = null;
        String mTempFilePath = getTempFilename();
        try {
            os = new FileOutputStream(mTempFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int read = 0;

        if (null != os) {
            while (isRecording) {
                read = mAudioRecord.read(data, 0, Constants.BUFFER_SIZE);

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopRecordAudioFile(Context context) {
        isRecording = false;
        NotificationTask.executeTask(context, Constants.ACTION_CLEAR_NOTIFICATION);
        if (null != mAudioRecord) {
            int i = mAudioRecord.getState();
            if (i == 1) mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
            if (mChronometer != null){
                mChronometer.stop();
            }
        }
        String mRecordedFilePath = getFilePath();
        copyWaveFile(getTempFilename(), mRecordedFilePath, Constants.BUFFER_SIZE);
        deleteTempFile(getTempFilename());

        java.util.Date date = Calendar.getInstance().getTime();

        long recordLengthInMlls = (SystemClock.elapsedRealtime() - mChronometer.getBase());

        LocalBroadcastManager mBroadcastManager = LocalBroadcastManager.getInstance(context);

        Intent intent1 = new Intent(RecordService.ACTION_RECORD);
        intent1.putExtra(Constants.RECORDED_FILE_PATH, mRecordedFilePath);
        mBroadcastManager.sendBroadcast(intent1);

        Record mRecord = new Record(mRecordedFilePath, date, recordLengthInMlls);

        AppRepository.getsInstance(context).insertNewRecord(mRecord);

        context.stopService(new Intent(context, RecordService.class));
    }

    private static String getFilePath(){
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, Constants.AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
        String mFilePath = file.getAbsolutePath() + "/" + System.currentTimeMillis() +
                Constants.AUDIO_RECORDER_FILE_EXT_WAV;
        Log.d(TAG, "File name is " + mFilePath + " **************");
        return mFilePath;
    }

    private static String getTempFilename() {
        String filepath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filepath, Constants.AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, Constants.AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists())
            tempFile.delete();

        String tempFileName = file.getAbsolutePath() + "/" + Constants.AUDIO_RECORDER_TEMP_FILE;
        Log.d(TAG, "Temporary file path : " + tempFileName + "**********");
        return tempFileName;
    }

    private static void copyWaveFile(String inFilename, String outFilename, int bufferSize) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = Constants.RECORDER_SAMPLERATE;
        int channels = 2;
        long byteRate = Constants.RECORDER_BPP * Constants.RECORDER_SAMPLERATE * channels / 8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            Log.d(TAG, "File size: " + totalDataLen);

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                            long totalDataLen, long longSampleRate, int channels,
                                            long byteRate) throws IOException {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = Constants.RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    private static void deleteTempFile(String tempFileName) {
        File file = new File(tempFileName);
        Log.d(TAG, "Temporary file " + file + " is deleted ***********");
        file.delete();
    }

    }
