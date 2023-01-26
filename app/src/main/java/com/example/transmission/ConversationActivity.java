package com.example.transmission;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ConversationActivity extends AppCompatActivity {

    private long chatId;

    public RadioService service;
    public boolean isBound = false;

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
            }
        });

        // do stuff
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

                // load messages
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };
        bindService(bindIntent, connection, 0);
    }
}