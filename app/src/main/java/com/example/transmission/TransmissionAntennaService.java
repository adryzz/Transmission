package com.example.transmission;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class TransmissionAntennaService extends Service {
    public TransmissionAntennaService() {
    }

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

        // make the persistent notification
        Notification notification =
                new NotificationCompat.Builder(this, getString(R.string.persistent_notification_channel_id))
                        .setContentTitle(getString(R.string.persistent_notification_title))
                        .setContentText("Nothing is happening in the service yet")
                        .setSmallIcon(R.drawable.round_sensors_24)
                        .setContentIntent(pendingIntent)
                        .addAction(stopAction)
                        .build();

        startForeground(9, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}