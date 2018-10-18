package com.mamaevaleksej.audiorecorder.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mamaevaleksej.audiorecorder.model.Record;

import java.util.List;

@Dao
public interface RecordsDAO {

//    @Query("SELECT * FROM records ORDER BY record_finished_time")
    @Query("SELECT * FROM records ORDER BY record_finished_time")
    LiveData<List<Record>> loadAllRecords();

    @Insert
    void insertRecord(Record record);

    @Query("DELETE FROM records WHERE id =:id")
    void deleteRecord(int id);

    @Query("SELECT COUNT(*) FROM records")
    int count();

}
