package de.htw_berlin.sharkandroidstack.modules.pki;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.inmemory.InMemoSharkKB;
import net.sharkfw.security.key.SharkKeyPairAlgorithm;
import net.sharkfw.security.key.storage.SharkKeyStorage;
import net.sharkfw.security.pki.SharkCertificate;
import net.sharkfw.security.pki.storage.SharkPkiStorage;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.modules.pki.system.CertManager;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogActivity;
import de.htw_berlin.sharkandroidstack.system_modules.log.LogManager;

/**
 * Created by m on 4/6/16.
 */
public class PkiMainActivity extends ParentActivity {
    private static final String LOG_ID = "pki";

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
        SharkKeyStorage sharkKeyStorage = CertManager.createAndStoreKeys(this.getApplicationContext(), SharkKeyPairAlgorithm.RSA, 1024);
        PeerSemanticTag me = InMemoSharkKB.createInMemoPeerSemanticTag("alice", "aliceId", "tcp://localhost:12341");
        SharkCertificate certificate = CertManager.createSelfSignedCertificate(me, sharkKeyStorage.getPublicKey(), 10);

        InMemoSharkKB kb = new InMemoSharkKB();

        try {
            SharkPkiStorage store = CertManager.createStore(kb, me, sharkKeyStorage.getPrivateKey());
            store.addSharkCertificate(certificate);
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            ;
            LogManager.addThrowable(LOG_ID, e);
        }
    }

    private void startLogActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(LogActivity.OPEN_LOG_ID_ON_START, LOG_ID);
        startActivity(intent);
    }

}
