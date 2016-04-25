package de.htw_berlin.sharkandroidstack.android;

import android.provider.Settings;

import com.crashlytics.android.Crashlytics;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;
import de.htw_berlin.sharkandroidstack.system_modules.settings.SettingsManager;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Mario Neises (mn-io) on 22.01.16.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        AndroidUtils.deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        SettingsManager.init(this);

        LogManager.init();
        LogManager.addEntry("sys", "Your device id is: " + AndroidUtils.deviceId, 1);

    }
}
