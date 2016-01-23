package de.htw_berlin.sharkandroidstack.system_modules.settings;


import android.os.Bundle;

import de.htw_berlin.sharkandroidstack.android.ParentActivity;

/**
 * Created by mn-io on 22.01.16.
 */
public class SettingsActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFragment(new SettingsFragment());
    }
}
