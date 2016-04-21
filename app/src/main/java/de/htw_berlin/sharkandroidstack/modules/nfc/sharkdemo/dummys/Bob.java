package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.dummys;

import android.widget.ArrayAdapter;

import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharksystem.android.peer.AndroidSharkEngine;

import de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo.NfcSharkDemoFragment;

/**
 * Created by mn-io on 29.01.2016.
 */
public class Bob extends Actor {
    public Bob(String name, NfcSharkDemoFragment fragment) {
        super(name, fragment);
    }

    public void initShark(AndroidSharkEngine engine) throws SharkProtocolNotSupportedException, SharkKBException {
        super.initShark(engine);
        kp = new TestKP(engine, new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) msgList.getAdapter();
                adapter.clear();
                adapter.addAll(kp.getReceivedData());
                adapter.notifyDataSetChanged();
            }
        });

    }

    public PeerSemanticTag getPeer() {
        return peer;
    }
}
