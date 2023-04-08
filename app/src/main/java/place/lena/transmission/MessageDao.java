package place.lena.transmission;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    void insert(Message message);

    @Update
    void update(Message message);

    @Query("SELECT * FROM message WHERE conversation_id = :conversationId ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<Message>> getRecentMessagesFromConversationLive(long conversationId, int limit);

    @Query("SELECT * FROM message WHERE conversation_id = :conversationId ORDER BY timestamp DESC LIMIT :limit")
    List<Message> getRecentMessagesFromConversation(long conversationId, int limit);

    @Query("SELECT * FROM message ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<Message>> getRecentMessages(int limit);

}