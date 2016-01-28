package de.htw_berlin.sharkandroidstack.sharkFW.peer;

import android.app.Activity;
import android.content.Context;

import net.sharkfw.kep.KEPStub;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.protocols.Stub;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.NfcMessageStub;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.WifiDirectStreamStub;

public class AndroidSharkEngine extends J2SEAndroidSharkEngine {
    Context context;
    WeakReference<Activity> activityRef;
    Stub currentStub;

    public AndroidSharkEngine(Context context) {
        super();
        this.context = context;
    }

    public AndroidSharkEngine(Activity activity) {
        super();
        this.context = activity.getApplicationContext();
        this.activityRef = new WeakReference<>(activity);
    }

    /*
     * Wifi Direct methods
     * @see net.sharkfw.peer.SharkEngine#createWifiDirectStreamStub(net.sharkfw.kep.KEPStub)
     */

    @Override
    protected Stub createWifiDirectStreamStub(KEPStub kepStub) throws SharkProtocolNotSupportedException {
        if (currentStub == null) {
            currentStub = new WifiDirectStreamStub(context, activityRef);
            currentStub.setHandler(kepStub);
        }
        return currentStub;
    }

    @Override
    public void startWifiDirect() throws SharkProtocolNotSupportedException, IOException {
        this.createWifiDirectStreamStub(this.getKepStub()).start();
    }

    public void stopWifiDirect() throws SharkProtocolNotSupportedException {
        currentStub.stop();
    }

    @Override
    protected Stub createNfcStreamStub(KEPStub kepStub) throws SharkProtocolNotSupportedException {
        if (currentStub == null) {
            currentStub = new NfcMessageStub(context, activityRef);
            currentStub.setHandler(kepStub);
        }
        return currentStub;
    }

    @Override
    public void startNfc() throws SharkProtocolNotSupportedException, IOException {
        this.createNfcStreamStub(this.getKepStub()).start();
    }

    @Override
    public void stopNfc() throws SharkProtocolNotSupportedException {
        this.createNfcStreamStub(this.getKepStub()).stop();
    }

    @Override
    protected Stub createBluetoothStreamStub(KEPStub kepStub) throws SharkProtocolNotSupportedException {
        throw new SharkProtocolNotSupportedException("TODO: Timm");
    }

    @Override
    public void startBluetooth() throws SharkProtocolNotSupportedException, IOException {
        throw new SharkProtocolNotSupportedException("TODO: Timm");
    }

    @Override
    public void stopBluetooth() throws SharkProtocolNotSupportedException {
        throw new SharkProtocolNotSupportedException("TODO: Timm");
    }

    @Override
    public Stub getProtocolStub(int type) throws SharkProtocolNotSupportedException {
        //TODO this function is called by the parent but the parent function itself look likes a big mess
        // and it does not look like it is designed to work with start/stop methods.
        return currentStub;
    }

    @Override
    public void sendKnowledge(Knowledge k, PeerSemanticTag recipient, KnowledgePort kp) throws SharkSecurityException, SharkKBException, IOException {
//        if (currentStub != null && currentStub instanceof WifiDirectStreamStub) {
//            WifiDirectStreamStub wifiStub = (WifiDirectStreamStub) currentStub;
//            recipient.setAddresses(new String[]{wifiStub.getConnectionStr()});
//        }
//        super.sendKnowledge(k, recipient, kp);
    }
}
