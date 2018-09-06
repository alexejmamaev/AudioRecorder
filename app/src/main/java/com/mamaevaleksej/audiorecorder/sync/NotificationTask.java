package com.mamaevaleksej.audiorecorder.sync;

import android.content.Context;

import com.mamaevaleksej.audiorecorder.Utils.Constants;
import com.mamaevaleksej.audiorecorder.Utils.NotificationUtils;

public class NotificationTask {

    public static void executeTask(Context context, String action){
        if (action.equals(Constants.ACTION_CLEAR_NOTIFICATION)){
            NotificationUtils.clearAllNotificatons(context);
        } else if(action.equals(Constants.ACTION_SHOW_NOTIFICATION)){
            NotificationUtils.fileIsBeingRecorded(context);
        }
    }
}
