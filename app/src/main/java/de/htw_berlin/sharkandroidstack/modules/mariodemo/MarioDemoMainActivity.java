package de.htw_berlin.sharkandroidstack.modules.mariodemo;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;

public class MarioDemoMainActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayoutResource(R.layout.module_mariodemo_activity);
        // xor: setFragment(new MyFragment)

        //action bar menu 1/2:
        setOptionsMenu(R.menu.module_mariodemo_menu);
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
        }

        if (!msg.isEmpty()) {
            Toast.makeText(this, "You pressed on '" + msg + "'", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
