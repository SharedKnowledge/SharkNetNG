package de.htw_berlin.sharkandroidstack.modules.mariodemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

public class MarioDemoMainActivity extends ParentActivity {

    private static final String LOG_ID = "mariodemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayoutResource(R.layout.module_mariodemo_activity);
        // xor: setFragment(new MyFragment)

        //action bar menu 1/2:
        setOptionsMenu(R.menu.module_mariodemo_menu);

        LogManager.registerLog(LOG_ID, "Demo");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogManager.unregisterLog(LOG_ID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //action bar menu 2/2:
        int id = item.getItemId();

        String msg = "";
        switch (id) {
            case R.id.mariodemo_menu_item_details:
                msg = "Details";
                break;
            case R.id.mariodemo_menu_item_example:
                msg = "Example";
                break;
            case R.id.mariodemo_menu_item_show_log:
                startLogActivity();
                return true;
        }

        if (!msg.isEmpty()) {
            Toast.makeText(this, "You pressed on '" + msg + "', this event is logged", Toast.LENGTH_SHORT).show();
            LogManager.addEntry(LOG_ID, "'" + msg + "' was pressed", 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startLogActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(LogActivity.OPEN_LOG_ID_ON_START, LOG_ID);
        startActivity(intent);
    }
}
