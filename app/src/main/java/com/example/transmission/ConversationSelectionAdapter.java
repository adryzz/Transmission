package com.example.transmission;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class ConversationSelectionAdapter extends RecyclerView.Adapter<ConversationSelectionAdapter.ConversationSelectionViewHolder> {

    Conversation[] conversations;

    public ConversationSelectionAdapter(Conversation[] convos){
        conversations = convos;
    }

    @Override
    public ConversationSelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_item, parent, false);
        return new ConversationSelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationSelectionViewHolder holder, int position) {
        // Bind data for the Conversation item to the views in the ConversationSelectionViewHolder
        Conversation conversation = conversations[position];
        holder.title.setText(conversation.name);
        holder.lastMessage.setText(conversation.lastMessageText);
        holder.timestamp.setText("2d");
        holder.unreadCount.setText("2");
        holder.chatId = conversation.uid;
    }

    @Override
    public int getItemCount() {
        return conversations.length;
    }

    class ConversationSelectionViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView lastMessage;
        TextView timestamp;
        TextView unreadCount;

        long chatId; // this will be set on bind

        ConversationSelectionViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.conversation_item_name);
            lastMessage = itemView.findViewById(R.id.conversation_item_last_message);
            timestamp = itemView.findViewById(R.id.conversation_item_timestamp);
            unreadCount = itemView.findViewById(R.id.conversation_item_unread_messages);

            itemView.setOnClickListener(view -> {
                // launch conversation activity with this conversation id
            });
        }
    }
}