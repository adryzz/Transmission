package com.example.transmission;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Conversation.class, Message.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ConversationDao conversationDao();

    public abstract MessageDao messageDao();
}