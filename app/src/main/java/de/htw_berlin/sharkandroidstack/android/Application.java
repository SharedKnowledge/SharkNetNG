package de.htw_berlin.sharkandroidstack.android;

import android.preference.PreferenceManager;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;
import de.htw_berlin.sharkandroidstack.system_modules.settings.KnowledgeBaseManager;
import de.htw_berlin.sharkandroidstack.system_modules.settings.SettingsManager;
import io.fabric.sdk.android.Fabric;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        AndroidUtils.deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        LogManager.init();
        LogManager.addEntry("sys", "Your device id is: " + AndroidUtils.deviceId, 1);

        SettingsManager.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SettingsManager.setDefaultValueOrMigrateToValid(SettingsManager.KEY_KB_PREFERENCES, KnowledgeBaseManager.implementationTypeDummy, KnowledgeBaseManager.implementationTypes);
    }
}
