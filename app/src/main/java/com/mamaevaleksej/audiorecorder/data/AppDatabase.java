package com.mamaevaleksej.audiorecorder.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {Record.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverterUtils.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static AppDatabase sInstance;
    private static final Object LOCK = new Object();
    private static final String APP_DATABASE_NAME = "records_database";

    public static AppDatabase getsInstance(Context context){
        if (sInstance == null){
           synchronized (LOCK){
               Log.d(TAG, "Creating new Room database *********");
               sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                       AppDatabase.APP_DATABASE_NAME).build();
           }
        }
        return sInstance;
    }

    public abstract RecordsDAO Dao();
}
