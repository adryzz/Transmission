package com.example.transmission;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ConversationDao {

    @Insert
    void insert(Conversation conversation);

    @Query("SELECT * FROM conversation")
    List<Conversation> getAll();

    @Query("SELECT * FROM conversation WHERE uid = :conversationId")
    Conversation getConversation(long conversationId);

    @Query("UPDATE conversation SET last_message_timestamp = :timestamp, last_message_text = :text WHERE uid = :conversationId")
    void updateLastMessage(long conversationId, long timestamp, String text);

}