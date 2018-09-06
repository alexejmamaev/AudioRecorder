package com.mamaevaleksej.audiorecorder.sync;

import android.app.IntentService;
import android.content.Intent;

public class RecordingNotificationService extends IntentService{

    public RecordingNotificationService() {
        super("RecordingNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null)
        NotificationTask.executeTask(this, action);
    }
}
