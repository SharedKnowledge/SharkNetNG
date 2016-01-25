package de.htw_berlin.sharkandroidstack.system_modules.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import java.util.Map;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogStreamHelper;

/**
 * Created by mn-io on 22.01.16.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system_module_settings);

        ListPreference listPreference = (ListPreference) findPreference(SettingsManager.KEY_KB_PREFERENCES);
        listPreference.setEntries(KnowledgeBaseManager.implementationTypes);
        listPreference.setEntryValues(KnowledgeBaseManager.implementationTypes);

        listPreference = (ListPreference) findPreference(SettingsManager.KEY_CONNECTION_LOG_LEVEL);
        listPreference.setEntries(LogStreamHelper.logLevelNames);
        listPreference.setEntryValues(LogStreamHelper.logLevelNames);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        Map<String, ?> allPreferences = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allPreferences.entrySet()) {
            setSummaryOfListPreference(entry.getKey());
            setSummaryOfEditTextPreferences(entry.getKey());
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
        setSummaryOfEditTextPreferences(key);

        //TODO: act on settings change
        if (SettingsManager.KEY_CONNECTION_LOG_LEVEL.equals(key)) {
            LogStreamHelper.setLogLevelFromPreferences();
        }
    }

    private void setSummaryOfListPreference(String key) {
        if (!(findPreference(key) instanceof ListPreference)) {
            return;
        }

        ListPreference preference = (ListPreference) findPreference(key);
        preference.setSummary(preference.getEntry());
    }

    private void setSummaryOfEditTextPreferences(String key) {
        if (!(findPreference(key) instanceof EditTextPreference)) {
            return;
        }

        EditTextPreference preference = (EditTextPreference) findPreference(key);
        preference.setSummary(preference.getText());
    }
}