package de.htw_berlin.sharkandroidstack.system_modules.settings;


import android.os.Bundle;

import de.htw_berlin.sharkandroidstack.android.ParentActivity;

public class SettingsActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFragment(new SettingsFragment());
    }
}
