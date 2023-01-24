package com.example.transmission;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.transmission.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
private int a = 69;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createNotificationChannels();

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);



        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This should in theory open the menu to add a new conversation, but it's clearly not implemented yet.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                String replyLabel = getResources().getString(R.string.reply_label);
                RemoteInput remoteInput = new RemoteInput.Builder("KEY_TEXT_REPLY")
                        .setLabel(replyLabel)
                        .build();

                Intent i = new Intent(getApplicationContext(), MainActivity.class);

                PendingIntent replyPendingIntent =
                        PendingIntent.getBroadcast(getApplicationContext(),
                                a,
                                i,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action action =
                        new NotificationCompat.Action.Builder(R.drawable.round_add_24,
                                getString(R.string.reply_label), replyPendingIntent)
                                .addRemoteInput(remoteInput)
                                .build();

                Person p = new Person.Builder().setName("sela").setBot(true).build();
                Person n = new Person.Builder().setName("me").setBot(true).build();
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), getString(R.string.messages_notification_channel_id))
                        .setStyle(new NotificationCompat.MessagingStyle(n)
                                .setConversationTitle("sela")
                                .addMessage("aaaa", 1, n) // Pass in null for user.
                                .addMessage("amogus sus?", 2, p)
                                .addMessage("sussy", 3, n)
                                .addMessage("yes sussy amogus sus", 4, p))
                        .addAction(action)
                        .setSmallIcon(R.drawable.signal_cellular_4_bar_24)
                        .setGroup("KEY_TEXT_REPLY")
                        .setColor(getColor(R.color.purple_500))
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(a, notification);

                a++;
            }
        });
    }

    private void createNotificationChannels() {
        // messages channel
        NotificationChannel messagesChannel = new NotificationChannel(getString(R.string.messages_notification_channel_id), getString(R.string.messages_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
        messagesChannel.setDescription(getString(R.string.messages_notification_channel_description));
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(messagesChannel);

        // persistent notification channel
        NotificationChannel persistentChannel = new NotificationChannel(getString(R.string.persistent_notification_channel_id), getString(R.string.persistent_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
        persistentChannel.setDescription(getString(R.string.persistent_notification_channel_description));
        notificationManager.createNotificationChannel(persistentChannel);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UsbDevice device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);

        Intent intent = new Intent(getApplicationContext(), TransmissionAntennaService.class);

        if (device != null)
        {
            intent.putExtra(UsbManager.EXTRA_DEVICE, device);
        }
        startForegroundService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}