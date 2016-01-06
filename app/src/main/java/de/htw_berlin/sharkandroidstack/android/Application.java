package de.htw_berlin.sharkandroidstack.android;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LogManager.init();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        setDefaultValue(prefs, "pref_key_kb", "aliceBob");
    }

    private void setDefaultValue(SharedPreferences prefs, String prefKey, String defaultValue) {
        String pref_key_kb = prefs.getString(prefKey, "");
        if (pref_key_kb.isEmpty()) {
            prefs.edit().putString(prefKey, defaultValue).commit();
        }
    }
}
