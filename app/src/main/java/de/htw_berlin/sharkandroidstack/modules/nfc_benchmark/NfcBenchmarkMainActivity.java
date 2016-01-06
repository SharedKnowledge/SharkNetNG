package de.htw_berlin.sharkandroidstack.modules.nfc_benchmark;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sharkfw.system.Util;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.Utils;
import de.htw_berlin.sharkandroidstack.android.KbTextViewWriter;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.setup.SharkStack;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;


public class NfcBenchmarkMainActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResource(R.layout.module_nfc_benchmark_activity);
        setOptionsMenu(R.menu.module_nfcbenchmark_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        String msg = "";
        switch (id) {
            case R.id.nfcbenchmark_menu_item_log:
                Intent intent = new Intent(this, LogActivity.class);
                startActivity(intent);
                break;
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
