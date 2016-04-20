package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.widget.Toast;

import net.sharksystem.android.protocols.nfc.NfcMessageStub;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

import static android.provider.Settings.ACTION_NFC_SETTINGS;
import static android.provider.Settings.ACTION_WIRELESS_SETTINGS;

/**
 * Created by mn-io on 22.01.16.
 */
public class NfcMainActivity extends ParentActivity {

    public final static int VIBRATION_DURATION = 500;
    public final static String LOG_ID = "NFC";
    public final static String SETTINGS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? ACTION_NFC_SETTINGS : ACTION_WIRELESS_SETTINGS;

    public static Runnable vibrate;
    private boolean hasMainFragment;
    private NfcWelcomeFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogManager.registerLog(LOG_ID, "NFC");
        setOptionsMenu(R.menu.module_nfc_menu);


        mainFragment = new NfcWelcomeFragment();
        changeFragment(mainFragment);

        final Vibrator vibrator = ((Vibrator) getApplicationContext().getSystemService(Activity.VIBRATOR_SERVICE));
        this.vibrate = new Runnable() {

            @Override
            public void run() {
                vibrator.vibrate(VIBRATION_DURATION);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();

        if (NfcAdapter.getDefaultAdapter(this) != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NfcAdapter.getDefaultAdapter(this).disableReaderMode(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (!hasMainFragment) {
            changeFragment(mainFragment);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent;
        switch (id) {
            case R.id.nfc_menu_item_log:
                intent = new Intent(this, LogActivity.class);
                intent.putExtra(LogActivity.OPEN_LOG_ID_ON_START, LOG_ID);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void changeFragment(Fragment fragment) {
        clearView();

        hasMainFragment = fragment == mainFragment;
        setFragment(fragment);
    }
}
