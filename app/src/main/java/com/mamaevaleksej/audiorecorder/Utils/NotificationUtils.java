package com.mamaevaleksej.audiorecorder.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.mamaevaleksej.audiorecorder.Constants;
import com.mamaevaleksej.audiorecorder.R;
import com.mamaevaleksej.audiorecorder.sync.NotificationTask;
import com.mamaevaleksej.audiorecorder.sync.RecordService;
import com.mamaevaleksej.audiorecorder.ui.RecorderActivity;

public class NotificationUtils {

    public static void clearAllNotificatons(Context context){
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null){
            notificationManager.cancelAll();
        }
    }

    public static void setNotification(Context context, String action) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.RECORDER_NOTIFICATION_CHANNEL_ID,
                    "Primary",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, Constants.RECORDER_NOTIFICATION_CHANNEL_ID)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setLargeIcon(notificationLargeIcon(context))
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentIntent(contentIntent(context))
                        .setAutoCancel(true);

        /* Notification while audio is being recorded */
        if (action.equals(NotificationTask.ACTION_SHOW_NOTIFICATION)){
            notificationBuilder.setSmallIcon(R.drawable.ic_record_notification)
                    .setContentText(context.getString(R.string.notification_recording))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText
                            (context.getString(R.string.notification_recording)))
                    .addAction(cancelRecording(context));

        }
        /* Notification reminder that there is an ignored record in the BD */
        else if(action.equals(NotificationTask.ACTION_REMIND_OF_IGNORED_RECORDS)){
            notificationBuilder.setSmallIcon(R.drawable.ic_mic_notification)
                    .setContentText(context.getString(R.string.notification_ignored_records))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText
                            (context.getString(R.string.notification_ignored_records)));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null)
        notificationManager.notify(Constants.RECORDING_NOTIFICATION_ID, notificationBuilder.build());
    }

    private static NotificationCompat.Action cancelRecording(Context context){
        Intent cancelIntent = new Intent(context, RecordService.class);
        cancelIntent.setAction(NotificationTask.ACTION_CLEAR_NOTIFICATION);
        PendingIntent cancelRecordingPendingIntent = PendingIntent.getService(
                context,
                Constants.ACTION_CANCEL_RECORDING_PENDING_INTENT_ID,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Action(
                R.drawable.ic_close_black_24dp,
                context.getString(R.string.notification_stop_recording),
                cancelRecordingPendingIntent);
    }

    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent = new Intent(context, RecorderActivity.class);
        return PendingIntent.getActivity(context,
                Constants.RECORDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap notificationLargeIcon(Context context){
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_mic_large_notification_64dp);
    }

}
