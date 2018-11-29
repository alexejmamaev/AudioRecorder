package com.mamaevaleksej.audiorecorder.sync;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class RecordReminder {

    private static final int REMINDER_INTERVAL_MINUTES = 1;
    private static final int REMINDER_INTERVAL_SECONDS =
            (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final String REMINDER_JOB_TAG = "hydration_reminder_tag";

    private static boolean sInitialized; // indicates if FirebaseJobDispatcher has been initialized before

    synchronized public static void ScheduleReminder(Context context){
        // only initialize FirebaseJobDispatcher once;
        if (sInitialized)  return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Creates Job to periodically create reminders to listen to new records */
        Job reminderJob = dispatcher.newJobBuilder()
                /* The Service that will be used to write to preferences */
                .setService(RecordReminderFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job. */
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS * 2))
                .setReplaceCurrent(true)
                .build();

                dispatcher.schedule(reminderJob);

        sInitialized = true;
    }
}
