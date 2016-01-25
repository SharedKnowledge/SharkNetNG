package de.htw_berlin.sharkandroidstack.system_modules.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogStreamHelper;

/**
 * Created by mn-io on 22.01.16.
 */
public class SettingsManager {

    public static final String KEY_KB_OWNER_PREFERENCES = "pref_key_kb_owner";
    public static final String KEY_KB_PREFERENCES = "pref_key_kb";
    public static final String KEY_CONNECTION_PREFERENCES = "pref_key_connection";
    public static final String KEY_CONNECTION_LOG_LEVEL = "pref_key_log_level";

    public static SharedPreferences prefs;

    public static void init(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        setDefaultValueOrMigrateToValid(KEY_KB_PREFERENCES, KnowledgeBaseManager.implementationTypeSimple, KnowledgeBaseManager.implementationTypes);
        setDefaultValue(KEY_KB_OWNER_PREFERENCES, AndroidUtils.deviceId);
        setDefaultValueOrMigrateToValid(KEY_CONNECTION_LOG_LEVEL, LogStreamHelper.logLevelNames[4], LogStreamHelper.logLevelNames);
    }

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
