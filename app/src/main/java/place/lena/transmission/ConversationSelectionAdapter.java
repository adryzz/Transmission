package place.lena.transmission;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import place.lena.transmission.R;

import java.util.ArrayList;
import java.util.List;

class ConversationSelectionAdapter extends RecyclerView.Adapter<ConversationSelectionAdapter.ConversationSelectionViewHolder> {

    List<Conversation> conversations;

    Context context;

    public ConversationSelectionAdapter(Context ctx){
        conversations = new ArrayList<Conversation>();
        context = ctx;
    }

    @Override
    public ConversationSelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_item, parent, false);


        return new ConversationSelectionViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ConversationSelectionViewHolder holder, int position) {
        // Bind data for the Conversation item to the views in the ConversationSelectionViewHolder
        Conversation conversation = conversations.get(position);
        holder.title.setText(conversation.name);
        holder.lastMessage.setText(conversation.lastMessageText);
        holder.timestamp.setText(Utils.timestampToText(conversation.lastMessageTimestamp));
        if (conversation.lastMessageTimestamp == 0) {
            holder.timestamp.setVisibility(View.GONE);
        }
        holder.unreadCount.setText(Integer.toString(conversation.unreadMessages));
        if (conversation.unreadMessages == 0) {
            holder.unreadCount.setVisibility(View.GONE);
        }
        holder.chatId = conversation.uid;
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void setItems(List<Conversation> convos, boolean fullUpdate) {
        conversations = convos;

        if (fullUpdate) {
            notifyDataSetChanged();
        } else {
            notifyItemInserted(convos.size() - 1);
        }
    }

    class ConversationSelectionViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView lastMessage;
        TextView timestamp;
        TextView unreadCount;

        long chatId; // this will be set on bind

        ConversationSelectionViewHolder(View itemView, Context context) {
            super(itemView);
            title = itemView.findViewById(R.id.conversation_item_name);
            lastMessage = itemView.findViewById(R.id.conversation_item_last_message);
            timestamp = itemView.findViewById(R.id.conversation_item_timestamp);
            unreadCount = itemView.findViewById(R.id.conversation_item_unread_messages);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra(context.getString(R.string.chat_id_intent_extra), chatId);
                context.startActivity(intent);
            });
        }
    }
}