package de.htw_berlin.sharkandroidstack.system_modules.intro;

import android.os.Bundle;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;

/**
 * Created by Mario Neises (mn-io) on 22.01.16.
 */
public class IntroActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayoutResource(R.layout.system_module_intro_activity);
    }
}
