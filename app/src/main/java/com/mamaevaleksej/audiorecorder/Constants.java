package com.mamaevaleksej.audiorecorder;

import android.media.AudioFormat;
import android.media.AudioRecord;

public class Constants {

    public final static int REQUEST_PERMISSIONS = 200;

    public final static String IS_RECORDING = "is_recording";

    public final static String RECORDED_FILE_PATH = "my_recorded_file_path";
    public final static String RECORDED_FILE_PATH_IS_MISSING = "file_missing";

    public final static String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    public final static String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";

    public final static int BUFFER_SIZE = AudioRecord.getMinBufferSize(8000,
            Constants.RECORDER_CHANNELS,
            Constants.RECORDER_AUDIO_ENCODING);

    public final static int RECORDER_BPP = 16;
    public final static int RECORDER_SAMPLERATE = 44100;
    public final static int RECORDER_CHANNELS = AudioFormat.CHANNEL_OUT_STEREO;
    public final static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public final static String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    public final static int BUFFER_ELEMENTS = 2048;

    /** NotificationTask.class **/
    public static final String ACTION_CLEAR_NOTIFICATION = "notification-clear";
    public static final String ACTION_SHOW_NOTIFICATION = "notification-show";

    /** NotificationUtils.class **/
    // Notification ID to access notification after it's been displayed.
    public static final int RECORDING_NOTIFICATION_ID = 1138;
    // PendingIntent id
    public static final int RECORDER_PENDING_INTENT_ID = 4217;
    // Notification channel id
    public static final String RECORDER_NOTIFICATION_CHANNEL_ID = "recorder_notification_channel";
    public static final int ACTION_CANCEL_RECORDING_PENDING_INTENT_ID = 19;

}
