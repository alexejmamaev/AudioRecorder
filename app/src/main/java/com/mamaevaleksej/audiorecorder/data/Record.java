package com.mamaevaleksej.audiorecorder.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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
    @NonNull
    private boolean listened;

    public Record(int id, String filePath, java.util.Date recordTime, long recordLength, boolean listened) {
        this.id = id;
        this.filePath = filePath;
        this.recordTime = recordTime;
        this.recordLength = recordLength;
        this.listened = listened;
    }

    @Ignore
    public Record(String filePath, java.util.Date recordTime, long recordLength, boolean listened) {
        this.filePath = filePath;
        this.recordTime = recordTime;
        this.recordLength = recordLength;
        this.listened = listened;
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

    public java.util.Date getRecordTime() {
        return recordTime;
    }

    public long getRecordLength() {
        return recordLength;
    }

    public void setRecordLength(long recordLength) {
        this.recordLength = recordLength;
    }

    public boolean isListened() {
        return listened;
    }

    public void setListened(boolean listened) {
        this.listened = listened;
    }
}
