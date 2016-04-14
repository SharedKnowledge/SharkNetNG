package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sharksystem.android.protocols.nfc.NfcMessageStub;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.modules.nfc.benchmark.NfcBenchmarkFragment;
import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.NfcSharkDemoFragment;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

import static android.provider.Settings.ACTION_NFC_SETTINGS;
import static android.provider.Settings.ACTION_WIRELESS_SETTINGS;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

/**
 * Created by mn-io on 22.01.16.
 */
public class NfcMainActivity extends ParentActivity {

    public final static String LOG_ID = "nfc";

    static NfcAdapter nfcAdapter;
    private int lastOptionsItemId = R.id.nfc_menu_item_welcome;
    static final String SETTINGS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? ACTION_NFC_SETTINGS : ACTION_WIRELESS_SETTINGS;

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
        setOptionsMenu(R.menu.module_nfc_menu);

        LogManager.registerLog(LOG_ID, "nfc module");
        setWelcomeScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (R.id.nfc_menu_item_welcome == lastOptionsItemId) {
            checkNfcSupport();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (nfcAdapter != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            nfcAdapter.disableReaderMode(this);
        }
    }

    private void checkNfcSupport() {
        if (nfcAdapter == null) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }

        TextView supportMessage = (TextView) findViewById(R.id.activity_nfc_support);
        Button enableButton = (Button) findViewById(R.id.activity_nfc_enable);

        String reason = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            reason = String.format(getString(R.string.activity_nfc_no_nfc_too_old), Build.VERSION.SDK_INT, Build.VERSION_CODES.KITKAT);
        } else if (nfcAdapter == null) {
            reason = getString(R.string.activity_nfc_no_nfc_no_adapter);
        }

        enableButton.setVisibility(GONE);
        supportMessage.setVisibility(GONE);
        findViewById(R.id.activity_nfc_arrow).setVisibility(GONE);
        findViewById(R.id.activtiy_nfc_start_here).setVisibility(GONE);

        if (null != reason) {
            reason = String.format(getString(R.string.activity_nfc_no_nfc), reason);
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
        if (lastOptionsItemId == id) {
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
                setWelcomeScreen();
                break;
            case R.id.nfc_menu_item_benchmark:
                clearView();
                setFragment(new NfcBenchmarkFragment());
                break;
            case R.id.nfc_menu_item_sharkdemo:
                clearView();
                setFragment(new NfcSharkDemoFragment());
                break;
        }

        lastOptionsItemId = id;

        return super.onOptionsItemSelected(item);
    }

    private void setWelcomeScreen() {
        clearView();
        setLayoutResource(R.layout.module_nfc_activity);
        checkNfcSupport();
    }

    public static void handleError(Context context, Throwable e) {
        if (e.getMessage().equals(NfcMessageStub.EXCEPTION_NFC_NOT_ENABLED)) {
            Intent intent = new Intent(SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Please enable NFC.", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = "An error occurred: " + e.getMessage() + "\nCheck Log for details";
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        LogManager.addThrowable(LOG_ID, e);
        e.printStackTrace();
    }
}
