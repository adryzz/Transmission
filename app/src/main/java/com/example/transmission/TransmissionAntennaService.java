package com.example.transmission;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

public class TransmissionAntennaService extends Service {
    public TransmissionAntennaService() {
    }

    BroadcastReceiver usbDisconnectionReceiver;

    UsbDevice usbDevice;

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
        usbDisconnectionReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null && device.getDeviceId() == usbDevice.getDeviceId()) {
                        // call your method that cleans up and closes communication with the device

                        NotificationManagerCompat.from(getApplicationContext()).notify(9,
                                createPersistentNotification(getString(R.string.persistent_notification_no_radio)));
                    }
                }
            }
        };

        getApplicationContext().registerReceiver(usbDisconnectionReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

        String notifText = getString(R.string.persistent_notification_no_radio);

        // check if the device is actually connected (and if we have the permission to open said device)
        if (device != null && manager.hasPermission(device)) {
            notifText = getString(R.string.persistent_notification_radios);
            usbDevice = device;
        }
        startForeground(9, createPersistentNotification(notifText));
        return super.onStartCommand(intent, flags, startId);
    }

    private Notification createPersistentNotification(String text) {
        // make an intent to start the main UI when pressing on the persistent notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);

        // make the "stop service" action
        Intent stopIntent = new Intent(getApplicationContext(), TransmissionAntennaService.class)
                .putExtra("stop", true);

        PendingIntent stopPendingIntent =
                PendingIntent.getForegroundService(getApplicationContext(),
                        9, stopIntent,
                        PendingIntent.FLAG_IMMUTABLE);

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
        Notification notification =
                new NotificationCompat.Builder(this, getString(R.string.persistent_notification_channel_id))
                        .setContentTitle(getText(R.string.persistent_notification_title))
                        .setContentText(text)
                        .setSmallIcon(R.drawable.round_sensors_24)
                        .setContentIntent(pendingIntent)
                        .addAction(settingsAction)
                        .addAction(stopAction)
                        .setVibrate(null)
                        .build();

        return notification;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        getApplicationContext().unregisterReceiver(usbDisconnectionReceiver);
        super.onDestroy();
    }
}