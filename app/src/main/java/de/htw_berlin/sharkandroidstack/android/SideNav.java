package de.htw_berlin.sharkandroidstack.android;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.mariodemo.MarioDemoMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.NfcMainActivity;
import de.htw_berlin.sharkandroidstack.modules.wifidirect.WifiDirectListActivity;
import de.htw_berlin.sharkandroidstack.modules.wifidirect.WifiDirectMainActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.settings.SettingsActivity;

/**
 * Created by mn-io on 22.01.16.
 */
public class SideNav {

    public final static Object[][] system_modules = new Object[][]{
            new Object[]{R.string.side_nav_item_settings, SettingsActivity.class},
            new Object[]{R.string.side_nav_item_log, LogActivity.class},
    };

    public final static Object[][] modules = new Object[][]{
            new Object[]{R.string.side_nav_item_nfc, NfcMainActivity.class},
            new Object[]{R.string.side_nav_item_mariodemo, MarioDemoMainActivity.class},
<<<<<<< f87361af87012e2e1dac9373a780816f71ccea5f
            new Object[]{R.string.side_nav_item_wifidirectList, WifiDirectListActivity.class}
=======
//            new Object[]{R.string.side_nav_item_wifidirect, WifiDirectMainActivity.class},
            new Object[]{R.string.side_nav_item_wifidirectList, WifiDirectListActivity.class},
>>>>>>> Implement WifiDirectListActivity.
    };
}
