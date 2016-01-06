package de.htw_berlin.sharkandroidstack.modules.nfc_benchmark;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;


public class NfcBenchmarkMainActivity extends ParentActivity {

    public static String LOG_ID = "nfc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResource(R.layout.module_nfc_benchmark_activity);
        setOptionsMenu(R.menu.module_nfcbenchmark_menu);

        LogManager.registerLog(LOG_ID, "nfc benchmark");
    }

    @Override
    protected void onResume() {
        super.onResume();

        LogManager.addEntry(LOG_ID, "test", 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        String msg = "";
        switch (id) {
            case R.id.nfcbenchmark_menu_item_log:
                Intent intent = new Intent(this, LogActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(LogActivity.OPEN_LOG_ID_ON_START, LOG_ID);
                startActivity(intent);
                return true;
            case R.id.nfcbenchmark_menu_item_benchmark:
                msg = "Example";
                break;
        }

        if (!msg.isEmpty()) {
            Toast.makeText(this, "You pressed on '" + msg + "'", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
