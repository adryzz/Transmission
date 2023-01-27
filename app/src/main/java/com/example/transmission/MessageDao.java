package com.example.transmission;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    void insert(Message message);

    @Query("SELECT * FROM message WHERE conversation_id = :conversationId ORDER BY timestamp ASC")
    List<Message> getAllMessages(long conversationId);

    @Query("SELECT * FROM message WHERE conversation_id = :conversationId ORDER BY timestamp DESC LIMIT :limit")
    List<Message> getRecentMessages(long conversationId, int limit);

}