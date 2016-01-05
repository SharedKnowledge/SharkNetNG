package de.htw_berlin.sharkandroidstack.system_modules.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import java.util.Map;

import de.htw_berlin.sharkandroidstack.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_module_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Map<String, ?> allPreferences = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allPreferences.entrySet()) {
            setSummaryOfListPreference(entry.getKey());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //String prefValue = sharedPreferences.getString(key, "");
        setSummaryOfListPreference(key);
        //TODO: act on settings change

    }

    private void setSummaryOfListPreference(String key) {
        if (!(findPreference(key) instanceof ListPreference)) {
            return;
        }

        ListPreference preference = (ListPreference) findPreference(key);
        preference.setSummary(preference.getEntry());
    }
}