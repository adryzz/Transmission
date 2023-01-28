package place.lena.transmission;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;

import place.lena.transmission.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.view.MenuItem;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import place.lena.transmission.databinding.ActivityMainBinding;

import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    public RadioService service;
    public boolean isServiceConnected = false;
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



        binding.fab.setOnClickListener(view -> {
            Snackbar.make(view, "This should in theory open the menu to add a new conversation, but it's clearly not implemented yet.", Snackbar.LENGTH_LONG).show();

            if (isServiceConnected) {
                service.createConversation("sela");
                Intent i = new Intent(this, NewConversationActivity.class);
                startActivity(i);
            }
        });
    }

    private void createNotificationChannels() {
        // ask for permissions in android 13
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[] { Manifest.permission.POST_NOTIFICATIONS },
                    0);
        }

        // messages channel
        NotificationChannel messagesChannel = new NotificationChannel(getString(R.string.messages_notification_channel_id), getString(R.string.messages_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
        messagesChannel.setDescription(getString(R.string.messages_notification_channel_description));

        notificationManager.createNotificationChannel(messagesChannel);

        // persistent notification channel
        NotificationChannel persistentChannel = new NotificationChannel(getString(R.string.persistent_notification_channel_id), getString(R.string.persistent_notification_channel_name), NotificationManager.IMPORTANCE_LOW);
        persistentChannel.setDescription(getString(R.string.persistent_notification_channel_description));
        persistentChannel.setSound(null, null);
        persistentChannel.enableVibration(false);
        notificationManager.createNotificationChannel(persistentChannel);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UsbDevice device = getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);

        Intent intent = new Intent(getApplicationContext(), RadioService.class);

        if (device != null)
        {
            intent.putExtra(UsbManager.EXTRA_DEVICE, device);
        }
        startForegroundService(intent);

        serviceConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isServiceConnected) {
            service.setNotificationStopButtonEnabled(true);
        }
    }

    void serviceConnection() {
        Intent bindIntent = new Intent(getApplicationContext(), RadioService.class);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder svc) {
                service = ((RadioService.RadioServiceBinder)svc).getService();
                isServiceConnected = true;

                //TODO: find better way
                FirstFragment fragment = (FirstFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main).getChildFragmentManager().getFragments().get(0);
                fragment.fillView();

                int radios = service.getConnectedRadios();

                //Fragment main = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);

                /*Toolbar toolbar = findViewById(R.id.toolbar);

                Menu menu = toolbar.getMenu();

                MenuItem radio0 = menu.findItem(R.id.action_antenna0_toggle);
                MenuItem radio1 = menu.findItem(R.id.action_antenna0_toggle);
                MenuItem radio2 = menu.findItem(R.id.action_antenna0_toggle);

                radio0.setVisible(radios > 0);
                radio1.setVisible(radios > 1);
                radio2.setVisible(radios > 2);

                radio0.setEnabled(radios > 0);
                radio1.setVisible(radios > 1);
                radio2.setVisible(radios > 2);*/
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isServiceConnected = false;
                /*Toolbar toolbar = findViewById(R.id.toolbar);

                Menu menu = toolbar.getMenu();

                MenuItem radio0 = menu.findItem(R.id.action_antenna0_toggle);
                MenuItem radio1 = menu.findItem(R.id.action_antenna0_toggle);
                MenuItem radio2 = menu.findItem(R.id.action_antenna0_toggle);

                radio0.setEnabled(false);
                radio1.setVisible(false);
                radio2.setVisible(false);*/
            }
        };
        bindService(bindIntent, connection, 0);
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