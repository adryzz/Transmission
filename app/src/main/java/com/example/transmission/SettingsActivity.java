package com.example.transmission;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
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

    void serviceConnection() {
        Intent bindIntent = new Intent(getApplicationContext(), RadioService.class);

        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                RadioService svc = ((RadioService.RadioServiceBinder)service).getService();
                int radios = svc.getConnectedRadios();
                SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.settings);
                PreferenceCategory radio0 = fragment.findPreference(getString(R.string.settings_category_radio0_id));
                PreferenceCategory radio1 = fragment.findPreference(getString(R.string.settings_category_radio1_id));
                PreferenceCategory radio2 = fragment.findPreference(getString(R.string.settings_category_radio2_id));

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
            public void onServiceDisconnected(ComponentName name) {
                SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.settings);
                PreferenceCategory radio0 = fragment.findPreference(getString(R.string.settings_category_radio0_id));
                PreferenceCategory radio1 = fragment.findPreference(getString(R.string.settings_category_radio1_id));
                PreferenceCategory radio2 = fragment.findPreference(getString(R.string.settings_category_radio2_id));

                radio0.setEnabled(false);
                radio0.setTitle(getText(R.string.settings_category_radio0_disconnected_name));
                radio1.setEnabled(false);
                radio1.setTitle(getText(R.string.settings_category_radio1_disconnected_name));
                radio2.setEnabled(false);
                radio2.setTitle(getText(R.string.settings_category_radio2_disconnected_name));
            }
        };
        bindService(bindIntent, connection, 0);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onResume() {
            super.onResume();

            Preference button = findPreference(getString(R.string.settings_reset_button_id));
            button.setOnPreferenceClickListener(preference -> {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.settings_reset_dialog_title)
                        .setCancelable(true)
                        .setMessage(R.string.settings_reset_dialog_message)
                        .setNeutralButton(R.string.settings_reset_dialog_cancel_text, (dialog, which) -> {

                        })
                        .setPositiveButton(R.string.settings_reset_dialog_reset_text, (dialog, which) -> {

                        })
                        .show();
                return true;
            });
        }
    }
}