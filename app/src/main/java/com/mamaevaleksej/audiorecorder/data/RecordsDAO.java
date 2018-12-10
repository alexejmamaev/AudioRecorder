package com.mamaevaleksej.audiorecorder.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RecordsDAO {

    @Query("SELECT * FROM records ORDER BY record_finished_time")
    LiveData<List<Record>> loadAllRecords();

    @Insert
    void insertRecord(Record record);

    @Query("DELETE FROM records WHERE id =:id")
    void deleteRecord(int id);

    @Query("SELECT * FROM records WHERE id =:id")
    Record getRecordById(int id);

    @Query("SELECT COUNT(*) FROM records")
    int count();

    @Query("DELETE FROM records")
    void nukeTable();

    @Query("UPDATE records SET listened = 1 WHERE id =:id")
    void setListened(int id);

    @Query("SELECT * FROM records WHERE listened = 0")
    List<Record> getAllIgnoredRecordsList();

}
