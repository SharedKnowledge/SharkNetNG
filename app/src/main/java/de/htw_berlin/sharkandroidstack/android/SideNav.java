package de.htw_berlin.sharkandroidstack.android;

public class SideNav {

    // TODO: use string resources, check whether classes instead of strings can be used
    public final static String[][] system_modules = new String[][]{
            new String[]{"Settings", "system_modules.settings.SettingsActivity"},
            new String[]{"Log", "system_modules.log.LogActivity"},
    };

    public final static String[][] modules = new String[][]{
            new String[]{"Wifi Direct Demo", null},
            new String[]{"Bluetooth Chat", null},
            new String[]{"PKI Manager", null},
            new String[]{"NFC Benchmark", "modules.nfc_benchmark.NfcBenchmarkMainActivity"},
            new String[]{"Mario Demo", "modules.mariodemo.MarioDemoMainActivity"},
            new String[]{"WifiDirect", "modules.wifidirect.WifiDirectMainActivity"},
    };
}
