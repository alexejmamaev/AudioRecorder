package com.mamaevaleksej.audiorecorder.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

@Database(entities = {Record.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverterUtils.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static AppDatabase sInstance;
    private static final Object LOCK = new Object();
    private static final String APP_DATABASE_NAME = "records_database";

    /* DB change schema migration */
//    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE records "
//                    + " ADD COLUMN listened INTEGER DEFAULT 0 not null");
//        }
//    };

    public static AppDatabase getsInstance(Context context){
        if (sInstance == null){
           synchronized (LOCK){
               Log.d(TAG, "Creating new Room database *********");
               sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                       AppDatabase.APP_DATABASE_NAME)
//                       .addMigrations(MIGRATION_1_2)
                       .build();
           }
        }
        return sInstance;
    }

    public abstract RecordsDAO Dao();
}
