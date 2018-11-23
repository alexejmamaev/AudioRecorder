package com.mamaevaleksej.audiorecorder.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "records")
public class Record {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "file_path")
    private String filePath;
    @ColumnInfo(name = "record_finished_time")
    private java.util.Date recordTime;
    @ColumnInfo(name = "record_length")
    private long recordLength;

    public Record(int id, String filePath, java.util.Date recordTime, long recordLength) {
        this.id = id;
        this.filePath = filePath;
        this.recordTime = recordTime;
        this.recordLength = recordLength;
    }

    @Ignore
    public Record(String filePath, java.util.Date recordTime, long recordLength) {
        this.filePath = filePath;
        this.recordTime = recordTime;
        this.recordLength = recordLength;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public java.util.Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(java.util.Date recordTime) {
        this.recordTime = recordTime;
    }

    public long getRecordLength() {
        return recordLength;
    }

    public void setRecordLength(int recordLength) {
        this.recordLength = recordLength;
    }
}
