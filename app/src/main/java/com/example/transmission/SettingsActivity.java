package com.example.transmission;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
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