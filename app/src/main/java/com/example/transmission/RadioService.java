package com.example.transmission;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

public class RadioService extends Service {
    public RadioService() {
    }
    private final IBinder binder = new RadioServiceBinder();
    BroadcastReceiver usbDisconnectionReceiver;
    BroadcastReceiver lowBatteryReceiver;
    UsbDevice usbDevice;
    MessageReceivedEventListener messageListener;
    String notificationText;
    boolean isStopButtonEnabled = true;

    Random random = new Random();
    AppDatabase database;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // stop if it has the bool stop
        // yes it can be stopped by a bad actor but whatever
        if (intent.hasExtra("stop"))
        {
            if (intent.getBooleanExtra("stop", false))
            {
                stopSelf();
            }
        }
        //TODO: fix this
        Executors.newSingleThreadExecutor().execute(() -> {
            database = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "transmission-database").allowMainThreadQueries().build();
        });


        setupIntentReceivers();

        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        String notifText = getString(R.string.persistent_notification_no_radio);

        // check if the device is actually connected (and if we have the permission to open said device)
        if (device != null && manager.hasPermission(device)) {
            notifText = getString(R.string.persistent_notification_radios, getConnectedRadios());
            usbDevice = device;
        }

        setupPreferences();

        startForeground(9, createPersistentNotification(notifText, false));
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createPersistentNotification(String text, boolean addStopButton) {
        // make an intent to start the main UI when pressing on the persistent notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        // make the "stop service" action
        Intent stopIntent = new Intent(getApplicationContext(), RadioService.class)
                .putExtra("stop", true);

        PendingIntent stopPendingIntent =
                PendingIntent.getForegroundService(getApplicationContext(),
                        9, stopIntent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action stopAction =
                new NotificationCompat.Action.Builder(R.drawable.round_sensors_24,
                        getString(R.string.persistent_notification_stop), stopPendingIntent)
                        .build();

        TaskStackBuilder builder = TaskStackBuilder.create(getApplicationContext());
        builder.addNextIntentWithParentStack(new Intent(getApplicationContext(), SettingsActivity.class));

        PendingIntent settingsIntent = builder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Action settingsAction =
                new NotificationCompat.Action.Builder(R.drawable.round_sensors_24,
                        getString(R.string.action_settings), settingsIntent)
                        .build();

        // make the persistent notification
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, getString(R.string.persistent_notification_channel_id))
                        .setContentTitle(getText(R.string.persistent_notification_title))
                        .setContentText(text)
                        .setSmallIcon(R.drawable.round_sensors_24)
                        .setContentIntent(pendingIntent)
                        .addAction(settingsAction)
                        .setVibrate(null);

        if (addStopButton) {
            notification.addAction(stopAction);
        }

        isStopButtonEnabled = addStopButton;
        notificationText = text;

        return notification.build();
    }

    private void setupIntentReceivers() {
        usbDisconnectionReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null && device.getDeviceId() == usbDevice.getDeviceId()) {
                        // call your method that cleans up and closes communication with the device

                        NotificationManagerCompat.from(getApplicationContext()).notify(9,
                                createPersistentNotification(getString(R.string.persistent_notification_no_radio), isStopButtonEnabled));
                    }
                }
            }
        };

        getApplicationContext().registerReceiver(usbDisconnectionReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        lowBatteryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (Intent.ACTION_BATTERY_LOW.equals(action)) {
                    // notify the user and stuff
                }
            }
        };

        getApplicationContext().registerReceiver(lowBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));


    }

    private void setupPreferences() {
        // setup listener for preference changes
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            // apply preference
        });

        // apply current preferences to device
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        getApplicationContext().unregisterReceiver(usbDisconnectionReceiver);
        getApplicationContext().unregisterReceiver(lowBatteryReceiver);
        super.onDestroy();
    }

    public class RadioServiceBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }
    }


    /* SERVICE INTERFACE AAAAAA */

    public void setNotificationStopButtonEnabled(boolean enabled) {
        NotificationManagerCompat.from(getApplicationContext())
                .notify(9, createPersistentNotification(notificationText, enabled));
    }

    public int getConnectedRadios() {
        return 2;
    }

    public void setRadioEnabled(int radio, boolean enabled) {

    }

    public List<Conversation> getConversations() {
        ConversationDao dao = database.conversationDao();
        return dao.getAll();
    }

    public Conversation getConversation(long id) {
        ConversationDao dao = database.conversationDao();
        return dao.getConversation(id);
    }

    public void createConversation(String name) {
        ConversationDao dao = database.conversationDao();
        Conversation c = new Conversation();
        c.uid = random.nextLong();
        c.creationTimestamp = Instant.now().getEpochSecond();
        c.name = name;
        dao.insert(c);
    }

    public void sendMessage(long channelId, String text) {
        long unixTime = Instant.now().getEpochSecond();
        Message message = new Message();
        message.conversationId = channelId;
        message.text = text;
        message.timestamp = unixTime;
        message.rssi = 0;
        message.uid = random.nextLong();
        message.flags = Message.FLAG_RECEIVED;

        MessageDao dao = database.messageDao();
        dao.insert(message);

        ConversationDao mDao = database.conversationDao();
        mDao.updateLastMessage(channelId, unixTime, text);
    }

    public List<Message> getMessagesForConversation(long channelId) {
        MessageDao dao = database.messageDao();
        return dao.getRecentMessages(channelId, 128);
    }

    public void setOnMessageReceivedEventListener(MessageReceivedEventListener listener) {
        messageListener = listener;
    }

    public interface MessageReceivedEventListener {
        void onMessageReceived(Message message);
    }


}