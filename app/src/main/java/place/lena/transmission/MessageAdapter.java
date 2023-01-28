package place.lena.transmission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import place.lena.transmission.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<Message> messages;
    Context context;

    RecyclerView recyclerView;
    long lastTime = Long.MAX_VALUE;
    int lastFlags = Integer.MAX_VALUE;

    public MessageAdapter(Context ctx, RecyclerView view){
        messages = new ArrayList<Message>();
        context = ctx;
        recyclerView = view;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_bubble, parent, false);


        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        // Bind data for the Message item to the views in the MessageViewHolder
        Message message = messages.get(position);
        holder.text.setText(message.text);
        holder.timestamp.setText(Utils.timestampToText(message.timestamp));

        if ((lastTime - message.timestamp > 300 || message.flags != lastFlags) || position == 0) {
            holder.timestampIconView.setVisibility(View.VISIBLE);

            if (!Utils.getFlag(message.flags, Message.FLAG_NOT_SENT)) {
                holder.icon.setImageDrawable(AppCompatResources.getDrawable(context, Utils.rssiToIconId(message.rssi)));
            }
            else {
                //TODO: move bubble to the right
                if (Utils.getFlag(message.flags, Message.FLAG_RECEIVED)) {
                    holder.icon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.round_check_circle_24));
                } else if (Utils.getFlag(message.flags, Message.FLAG_SENT)) {
                    holder.icon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.round_check_circle_outline_24));
                }
                holder.icon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.round_schedule_24));
            }
        } else {
            holder.timestampIconView.setVisibility(View.GONE);
        }

        // handle updates
        if (position == 0) {
            if (messages.size() > 1) {
                if (message.timestamp - messages.get(1).timestamp < 300) {
                    MessageViewHolder previous = (MessageViewHolder)recyclerView.findViewHolderForAdapterPosition(1);

                    // since the elements go from 0 to the top, and every time a new element
                    // is present it takes the spot 0, when counting this way when first loading
                    // the view, the second item isn't initialized yet, so it's null, and we need to handle that.
                    if (previous != null) {
                        previous.timestampIconView.setVisibility(View.GONE);
                    }
                }
            }
        }

        lastTime = message.timestamp;
        lastFlags = message.flags;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setItems(List<Message> msg, boolean fullUpdate) {
        messages = msg;

        if (fullUpdate) {
            notifyDataSetChanged();
        } else {
            notifyItemInserted(0);
        }

    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        TextView timestamp;
        ImageView icon;
        View timestampIconView;

        @SuppressWarnings("unused")
        long chatId; // this will be set on bind

        MessageViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.message_text);
            timestamp = itemView.findViewById(R.id.timestamp_text);
            icon = itemView.findViewById(R.id.message_icon);
            timestampIconView = itemView.findViewById(R.id.icon_timestamp_frame);

            itemView.setOnLongClickListener(v -> {
                Snackbar.make(itemView, "This should open a menu or something idk", Snackbar.LENGTH_LONG).show();
                return true;
            });
        }
    }
}