package de.htw_berlin.sharkandroidstack.android;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.modules.mariodemo.MarioDemoMainActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc_benchmark.NfcBenchmarkMainActivity;
import de.htw_berlin.sharkandroidstack.modules.wifidirect.WifiDirectMainActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.settings.SettingsActivity;

public class SideNav {

    public final static Object[][] system_modules = new Object[][]{
            new Object[]{R.string.side_nav_item_settings, SettingsActivity.class},
            new Object[]{R.string.side_nav_item_log, LogActivity.class},
    };

    public final static Object[][] modules = new Object[][]{
            new Object[]{R.string.side_nav_item_nfcbenchmark, NfcBenchmarkMainActivity.class},
            new Object[]{R.string.side_nav_item_mariodemo, MarioDemoMainActivity.class},
            new Object[]{R.string.side_nav_item_wifidirect, WifiDirectMainActivity.class},
    };
}
