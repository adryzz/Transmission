package com.example.transmission;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<Message> messages;

    Context context;

    long lastTime = Long.MAX_VALUE;
    int lastFlags = Integer.MAX_VALUE;

    public MessageAdapter(List<Message> msg, Context ctx){
        messages = msg;
        context = ctx;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_bubble, parent, false);


        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // Bind data for the Conversation item to the views in the MessageViewHolder
        Message message = messages.get(position);
        holder.text.setText(message.text);
        holder.timestamp.setText(Utils.timestampToText(message.timestamp));

        if (lastTime - message.timestamp > 300 || message.flags != lastFlags) {
            if (!Utils.getFlag(message.flags, Message.FLAG_NOT_SENT)) {
                holder.icon.setImageDrawable(context.getDrawable(Utils.rssiToIconId(message.rssi)));
            }
            else {
                //TODO: move bubble to the right
                if (Utils.getFlag(message.flags, Message.FLAG_RECEIVED)) {
                    holder.icon.setImageDrawable(context.getDrawable(R.drawable.round_check_circle_24));
                } else if (Utils.getFlag(message.flags, Message.FLAG_SENT)) {
                    holder.icon.setImageDrawable(context.getDrawable(R.drawable.round_check_circle_outline_24));
                }
                holder.icon.setImageDrawable(context.getDrawable(R.drawable.round_schedule_24));
            }
        } else {
            holder.timestampIconView.setVisibility(View.GONE);
        }
        lastTime = message.timestamp;
        lastFlags = message.flags;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView timestamp;
        ImageView icon;
        View timestampIconView;

        long chatId; // this will be set on bind

        MessageViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_text);
            timestamp = itemView.findViewById(R.id.timestamp_text);
            icon = itemView.findViewById(R.id.message_icon);
            timestampIconView = itemView.findViewById(R.id.icon_timestamp_frame);

            itemView.setOnLongClickListener(v -> {
                Snackbar.make(itemView, "This should open a menu or something idk", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            });
        }
    }
}