package de.htw_berlin.sharkandroidstack.modules.pki;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.modules.pki.system.CertManager;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

/**
 * Created by mn-io on 06.04.16.
 */
public class PkiMainActivity extends ParentActivity {
    private static final String LOG_ID = "pki";

    public static CertManager certManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupPki();

        setFragment(new CertManagerFragment());

        setOptionsMenu(R.menu.module_pki_menu);
        LogManager.registerLog(LOG_ID, "PKI");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.pki_menu_item_show_log:
                startLogActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupPki() {
        PeerSemanticTag me = InMemoSharkKB.createInMemoPeerSemanticTag(AndroidUtils.deviceId, AndroidUtils.deviceId + "_Id", "tcp://" + AndroidUtils.deviceId);

        String text = "";
        try {
            if (certManager == null) {
                certManager = new CertManager(this.getApplicationContext(), me);
                text = "New CertManager created.";

            } else {
                text = "Found existing CertManager.";
                Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            text = "An error occurred: " + e.getMessage();
            LogManager.addThrowable(LOG_ID, e);
        }

        Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void startLogActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(LogActivity.OPEN_LOG_ID_ON_START, LOG_ID);
        startActivity(intent);
    }

}
