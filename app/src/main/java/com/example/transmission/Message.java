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
}
