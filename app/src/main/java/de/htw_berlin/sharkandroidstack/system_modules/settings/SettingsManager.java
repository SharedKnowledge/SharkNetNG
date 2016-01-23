package de.htw_berlin.sharkandroidstack.system_modules.settings;

import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mn-io on 22.01.16.
 */
public class SettingsManager {

    public static final String KEY_KB_PREFERENCES = "pref_key_kb";
    public static final String KEY_CONNECTION_PREFERENCES = "pref_key_connection";

    public static SharedPreferences prefs;

    public static void setDefaultValueOrMigrateToValid(String prefKey, String defaultValue, String[] validTypes) {
        final List<String> strings = Arrays.asList(validTypes);
        if (!strings.contains(defaultValue)) {
            throw new IllegalArgumentException("Default value " + defaultValue + " has to be a valid type.");
        }

        String value = prefs.getString(prefKey, "");
        if (value.isEmpty() || !strings.contains(value)) {
            prefs.edit().putString(prefKey, defaultValue).apply();
        }
    }

    public static void setDefaultValue(String prefKey, String defaultValue) {
        String value = prefs.getString(prefKey, "");
        if (value.isEmpty()) {
            prefs.edit().putString(prefKey, defaultValue).apply();
        }
    }

    public static String getValue(String prefKey, String defaultValue) {
        return prefs.getString(prefKey, defaultValue);
    }

    public static String getValue(String prefKey) {
        return prefs.getString(prefKey, "");
    }
}
