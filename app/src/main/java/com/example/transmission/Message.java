package com.example.transmission;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {

    @PrimaryKey
    public long uid;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "timestamp")
    public long timestamp;

    @ColumnInfo(name = "conversation_id")
    public long conversationId;

    @ColumnInfo(name = "rssi")
    public int rssi;

    @ColumnInfo(name = "flags")
    public int flags;

    public static final int FLAG_NOT_SENT = 2;
    public static final int FLAG_SENT = 4;
    public static final int FLAG_RECEIVED = 8;

    public static final int FLAG_READ = 16;
}
