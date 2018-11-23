package com.mamaevaleksej.audiorecorder.Utils;

import android.app.ActivityManager;
import android.content.Context;

public class ServiceUtils {

    // Helper method to check if service is running
    public static boolean myServiceIsRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
