package com.example.transmission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private long chatId;

    public RadioService service;
    public boolean isBound = false;

    RecyclerView recyclerView;

    Conversation currentConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        chatId = getIntent().getLongExtra(getString(R.string.chat_id_intent_extra), 0);

        if (chatId == 0) {
            finish();
        }

        //set up sendMessage
        EditText editText = findViewById(R.id.message_text);

        ImageButton sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(v -> {
            if (isBound) {
                service.sendMessage(chatId, editText.getText().toString());
                editText.getText().clear();
                // TODO: refresh
            }
        });

        // do stuff

        recyclerView = findViewById(R.id.messages_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent bindIntent = new Intent(getApplicationContext(), RadioService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder svc) {
                service = ((RadioService.RadioServiceBinder)svc).getService();
                isBound = true;
                service.setNotificationStopButtonEnabled(false);

                currentConversation = service.getConversation(chatId);
                getSupportActionBar().setTitle(currentConversation.name);
                // load messages

                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                layoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(layoutManager);
                List<Message> messages = service.getMessagesForConversation(chatId);

                MessageAdapter adapter = new MessageAdapter(messages, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
        bindService(bindIntent, connection, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isBound) {
            service.setNotificationStopButtonEnabled(true);
        }
    }
}