package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.os.Handler;

import net.sharkfw.asip.engine.ASIPConnection;
import net.sharkfw.asip.engine.ASIPInMessage;
import net.sharkfw.peer.SharkEngine;
import net.sharkfw.system.L;

import java.io.InputStream;

import de.htw_berlin.sharkandroidstack.modules.nfc.RawKp;

/**
 * Created by m on 4/25/16.
 */
public class SimpleRawKp extends RawKp {

    private Runnable updater;
    private String[] receivedData;
    private Handler handler = new Handler();

    public SimpleRawKp(SharkEngine se, Runnable updater) {
        super(se);
        this.updater = updater;
    }

    @Override
    protected void handleRaw(InputStream is, ASIPConnection asipConnection) {
        ASIPInMessage inMessage = (ASIPInMessage) asipConnection;
        InputStream is2 = inMessage.getRaw();
        try {
            byte[] buffer = new byte[is2.available()];
            is2.read(buffer);
            receivedData = deserializeAsStrings(buffer);
        } catch (Exception e) {
            L.d(e.getMessage());
            e.printStackTrace();
        }

        if (updater != null) {
            handler.post(updater);
        }

        super.handleRaw(is, asipConnection);
    }

    public String[] getReceivedData() {
        return receivedData;
    }
}
