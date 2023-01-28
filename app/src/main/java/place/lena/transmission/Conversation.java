package place.lena.transmission;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Conversation {

    @PrimaryKey
    public long uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "last_message_timestamp")
    public long lastMessageTimestamp;

    @ColumnInfo(name = "last_message_text")
    public String lastMessageText;

    @ColumnInfo(name = "creation_timestamp")
    public long creationTimestamp;

    @ColumnInfo(name = "unread_messages")
    public int unreadMessages;
}
