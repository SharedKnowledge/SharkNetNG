package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

import static android.provider.Settings.ACTION_NFC_SETTINGS;
import static android.provider.Settings.ACTION_WIRELESS_SETTINGS;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;


public class NfcMainActivity extends ParentActivity {

    public static String LOG_ID = "nfc";
    public static NfcAdapter nfcAdapter;

    final static OnClickListener enableNfcClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            String settings = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ?
                    ACTION_NFC_SETTINGS : ACTION_WIRELESS_SETTINGS;
            v.getContext().startActivity(new Intent(settings));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResource(R.layout.module_nfc_activity);
        setOptionsMenu(R.menu.module_nfc_menu);

        LogManager.registerLog(LOG_ID, "nfc module");
    }

    @Override
    protected void onResume() {
        super.onResume();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        checkNfcSupport();
    }

    private void checkNfcSupport() {
        String reason = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            reason = "Your Android Version is too old (Found API: " + Build.VERSION.SDK_INT + "). " +
                    "Requires at least Kitkat (API:" + Build.VERSION_CODES.KITKAT + ")";
        } else if (nfcAdapter == null) {
            reason = "NFC Adapter not found";
        }

        TextView supportMessage = (TextView) findViewById(R.id.activity_nfc_support);
        Button enableButton = (Button) findViewById(R.id.activity_nfc_enable);

        enableButton.setVisibility(GONE);
        supportMessage.setVisibility(GONE);
        findViewById(R.id.activity_nfc_arrow).setVisibility(GONE);
        findViewById(R.id.activtiy_nfc_start_here).setVisibility(GONE);

        if (null != reason) {
            reason = "NFC not support: " + reason;
            LogManager.addEntry(LOG_ID, reason, 1);
            supportMessage.setText(reason);
            supportMessage.setVisibility(VISIBLE);
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            enableButton.setOnClickListener(enableNfcClickListener);
            enableButton.setVisibility(VISIBLE);
            return;
        }

        findViewById(R.id.activity_nfc_arrow).setVisibility(VISIBLE);
        findViewById(R.id.activtiy_nfc_start_here).setVisibility(VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ((nfcAdapter == null || !nfcAdapter.isEnabled()) && R.id.nfc_menu_item_log != id) {
            return false;
        }

        Intent intent;
        switch (id) {
            case R.id.nfc_menu_item_log:
                intent = new Intent(this, LogActivity.class);
                intent.putExtra(LogActivity.OPEN_LOG_ID_ON_START, LOG_ID);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            case R.id.nfc_menu_item_welcome:
                clearView();
                setLayoutResource(R.layout.module_nfc_activity);
                break;
            case R.id.nfc_menu_item_benchmark:
                clearView();
                setFragment(new NfcBenchmarkFragment());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void prepareReceiving(NfcAdapter.ReaderCallback readerCallback) {
        System.out.println("Mario: prepare receiving");

        // http://stackoverflow.com/questions/27939030/alternative-way-for-enablereadermode-to-work-with-android-apis-lesser-than-19
        final int flags = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
        nfcAdapter.enableReaderMode(this, readerCallback, flags, null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    void prepareSending() {
        System.out.println("Mario: prepare sending");

        //SmartCardEmulationService.setInput(input);
        nfcAdapter.disableReaderMode(this);
    }

}
