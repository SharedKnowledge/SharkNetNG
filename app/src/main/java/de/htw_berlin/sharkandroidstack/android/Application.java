package de.htw_berlin.sharkandroidstack.android;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;
import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        AndroidUtils.deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

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
