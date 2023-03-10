package place.lena.transmission;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import place.lena.transmission.R;

public class SettingsActivity extends AppCompatActivity {

    public RadioService service;
    public boolean isBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        serviceConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isBound) {
            service.setNotificationStopButtonEnabled(true);
        }
    }

    void serviceConnection() {
        Intent bindIntent = new Intent(getApplicationContext(), RadioService.class);

        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder svc) {
                service = ((RadioService.RadioServiceBinder)svc).getService();
                isBound = true;
                service.setNotificationStopButtonEnabled(false);
                SettingsFragmentBase fragment = (SettingsFragmentBase)getSupportFragmentManager().findFragmentById(R.id.settings);
                fragment.onServiceConnection(service);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
                SettingsFragmentBase fragment = (SettingsFragmentBase)getSupportFragmentManager().findFragmentById(R.id.settings);
                fragment.onServiceDisconnection();
            }
        };
        bindService(bindIntent, connection, 0);
    }

    public static abstract class SettingsFragmentBase extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            // this makes sure the new screen is up to date with the service

            SettingsActivity activity = (SettingsActivity)getActivity();

            if ((activity).isBound) {
                onServiceConnection(activity.service);
            } else {
                onServiceDisconnection();
            }
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        public abstract void onServiceConnection(RadioService service);

        public abstract void onServiceDisconnection();
    }

    public static class SettingsFragment extends SettingsFragmentBase {

        int versionClicks = 0;
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            super.onCreatePreferences(savedInstanceState, rootKey);

            Preference credits = findPreference(getString(R.string.settings_version_id));

            credits.setOnPreferenceClickListener(preference -> {
                versionClicks++;
                if (versionClicks == 7) {
                    // easter egg
                    Toast.makeText(getContext(), getString(R.string.settings_version_easter_egg_text), Toast.LENGTH_SHORT).show();
                }
                return versionClicks == 7;
            });

            Preference repo = findPreference(getString(R.string.settings_repository_id));

            repo.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.settings_repository_url)));

                startActivity(intent);
                return false;
            });
        }

        @Override
        public void onServiceConnection(RadioService service) { }

        @Override
        public void onServiceDisconnection() { }
    }

    public static class RadioSettingsFragment extends SettingsFragmentBase {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.radio_preferences, rootKey);

            super.onCreatePreferences(savedInstanceState, rootKey);
        }

        @Override
        public void onResume() {
            super.onResume();

            Preference button = findPreference(getString(R.string.settings_reset_radio_button_id));
            button.setOnPreferenceClickListener(preference -> {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.settings_reset_radio_dialog_title)
                        .setCancelable(true)
                        .setMessage(R.string.settings_reset_radio_dialog_message)
                        .setNeutralButton(R.string.settings_reset_radio_dialog_cancel_text, (dialog, which) -> {

                        })
                        .setPositiveButton(R.string.settings_reset_radio_dialog_reset_text, (dialog, which) -> {
                            PreferenceManager.setDefaultValues(requireContext(), R.xml.radio_preferences, true);
                        })
                        .show();
                return true;
            });
        }

        @Override
        public void onServiceConnection(RadioService service) {
            int radios = service.getConnectedRadios();
            PreferenceCategory radio0 = findPreference(getString(R.string.settings_category_radio0_id));
            PreferenceCategory radio1 = findPreference(getString(R.string.settings_category_radio1_id));
            PreferenceCategory radio2 = findPreference(getString(R.string.settings_category_radio2_id));

            if (radios > 0) {
                radio0.setEnabled(true);
                radio0.setTitle(getText(R.string.settings_category_radio0_name));
            }

            if (radios > 1) {
                radio1.setEnabled(true);
                radio1.setTitle(getText(R.string.settings_category_radio1_name));
            }

            if (radios > 2) {
                radio2.setEnabled(true);
                radio2.setTitle(getText(R.string.settings_category_radio2_name));
            }
        }

        @Override
        public void onServiceDisconnection() {
            PreferenceCategory radio0 = findPreference(getString(R.string.settings_category_radio0_id));
            PreferenceCategory radio1 = findPreference(getString(R.string.settings_category_radio1_id));
            PreferenceCategory radio2 = findPreference(getString(R.string.settings_category_radio2_id));

            radio0.setEnabled(false);
            radio0.setTitle(getText(R.string.settings_category_radio0_disconnected_name));
            radio1.setEnabled(false);
            radio1.setTitle(getText(R.string.settings_category_radio1_disconnected_name));
            radio2.setEnabled(false);
            radio2.setTitle(getText(R.string.settings_category_radio2_disconnected_name));
        }
    }

    public static class MessageSettingsFragment extends SettingsFragmentBase {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.message_preferences, rootKey);

            super.onCreatePreferences(savedInstanceState, rootKey);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onServiceConnection(RadioService service) {

        }

        @Override
        public void onServiceDisconnection() {

        }
    }

    public static class AdvancedSettingsFragment extends SettingsFragmentBase {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.advanced_preferences, rootKey);

            super.onCreatePreferences(savedInstanceState, rootKey);

            Preference button = findPreference(getString(R.string.settings_reset_advanced_button_id));
            button.setOnPreferenceClickListener(preference -> {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.settings_reset_advanced_dialog_title)
                        .setCancelable(true)
                        .setMessage(R.string.settings_reset_advanced_dialog_message)
                        .setNeutralButton(R.string.settings_reset_advanced_dialog_cancel_text, (dialog, which) -> {

                        })
                        .setPositiveButton(R.string.settings_reset_advanced_dialog_reset_text, (dialog, which) -> {
                            PreferenceManager.setDefaultValues(requireContext(), R.xml.advanced_preferences, true);
                        })
                        .show();
                return true;
            });
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onServiceConnection(RadioService service) {

        }

        @Override
        public void onServiceDisconnection() {

        }
    }
}