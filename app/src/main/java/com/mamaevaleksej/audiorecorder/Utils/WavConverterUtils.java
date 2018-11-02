package com.mamaevaleksej.audiorecorder.Utils;

import android.media.AudioTrack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class WavConverterUtils {

    private static final String TAG = WavConverterUtils.class.getSimpleName();

    private static FileInputStream is;

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

