package com.mamaevaleksej.audiorecorder.Utils;

import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WavConverterUtils {

    private static final String TAG = WavConverterUtils.class.getSimpleName();

    private static FileInputStream is;

    public static String getFilePath(){
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


    public static String getTempFilename() {
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

    public static void deleteTempFile(String tempFileName) {
        File file = new File(tempFileName);
        Log.d(TAG, "Temporary file " + file + " is deleted ***********");
        file.delete();
    }

    public static void copyWaveFile(String inFilename, String outFilename, int bufferSize) {
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


    public static boolean playReverse(File recordedFile, AudioTrack audioTrack) {

        audioTrack.play();

        byte[] buffer = new byte[Constants.BUFFER_ELEMENTS];

        try {
            is = new FileInputStream(recordedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            buffer = convertStreamToByteArray(is, Constants.BUFFER_ELEMENTS);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        audioTrack.write(buffer, 0, buffer.length);
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
        int i = Integer.MAX_VALUE;
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

}

