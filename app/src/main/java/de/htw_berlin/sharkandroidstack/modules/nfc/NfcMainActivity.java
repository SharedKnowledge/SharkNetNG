package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;


public class NfcMainActivity extends ParentActivity {

    public static String LOG_ID = "nfc";

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

        LogManager.addEntry(LOG_ID, "test", 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        switch (id) {
            case R.id.nfc_menu_item_log:
                intent = new Intent(this, LogActivity.class);
                intent.putExtra(LogActivity.OPEN_LOG_ID_ON_START, LOG_ID);
                return true;
            case R.id.nfc_menu_item_benchmark:
                intent = new Intent(this, LogActivity.class);
                startActivity(intent);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        return super.onOptionsItemSelected(item);
    }


}
