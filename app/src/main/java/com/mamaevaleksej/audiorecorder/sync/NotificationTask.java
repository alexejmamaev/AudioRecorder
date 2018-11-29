package com.mamaevaleksej.audiorecorder.sync;

import android.content.Context;

import com.mamaevaleksej.audiorecorder.Utils.NotificationUtils;

public class NotificationTask {

    public static final String ACTION_CLEAR_NOTIFICATION = "notification-clear";
    public static final String ACTION_SHOW_NOTIFICATION = "notification-show";
    public static final String ACTION_REMIND_OF_IGNORED_RECORDS = "firebase_notification-show";


    public static void executeTask(Context context, String action){
        switch (action) {
            case ACTION_CLEAR_NOTIFICATION:
                NotificationUtils.clearAllNotificatons(context);
                break;
            case ACTION_SHOW_NOTIFICATION:
                NotificationUtils.setNotification(context, ACTION_SHOW_NOTIFICATION);
                break;
            case ACTION_REMIND_OF_IGNORED_RECORDS:
                NotificationUtils.setNotification(context, ACTION_REMIND_OF_IGNORED_RECORDS);
                break;
        }
    }
}
