package de.htw_berlin.sharkandroidstack.sharkFW.peer;

import android.content.Context;

import net.sharkfw.kep.KEPStub;
import net.sharkfw.kep.SharkProtocolNotSupportedException;
import net.sharkfw.knowledgeBase.Knowledge;
import net.sharkfw.knowledgeBase.PeerSemanticTag;
import net.sharkfw.knowledgeBase.SharkKBException;
import net.sharkfw.peer.J2SEAndroidSharkEngine;
import net.sharkfw.peer.KnowledgePort;
import net.sharkfw.protocols.Stub;
import net.sharkfw.protocols.wifidirect.WifiDirectStreamStub;
import net.sharkfw.system.SharkSecurityException;

import java.io.IOException;

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.NfcStreamStub;

public class AndroidSharkEngine extends J2SEAndroidSharkEngine {
    Context _context;
    Stub currentStub;

    public AndroidSharkEngine(Context context) {
        super();
        _context = context;
    }


    public Context getContext() {
        return _context;
    }

    /*
     * Wifi Direct methods
     * @see net.sharkfw.peer.SharkEngine#createWifiDirectStreamStub(net.sharkfw.kep.KEPStub)
     */

    @Override
    protected Stub createWifiDirectStreamStub(KEPStub kepStub) throws SharkProtocolNotSupportedException {
        if (currentStub != null) {
            currentStub.stop();
        }
        //TODO: this (SharkEngine not used for WifiDirectStreamStub), kebStub should be set by setHandler()
        currentStub = new WifiDirectStreamStub(getContext(), this, kepStub);
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
        if (currentStub != null) {
            currentStub.stop();
        }
        currentStub = new NfcStreamStub(getContext());
        currentStub.setHandler(kepStub);
        return currentStub;
    }

    @Override
    public void startNfc() throws SharkProtocolNotSupportedException, IOException {
        this.createNfcStreamStub(this.getKepStub()).start();
    }

    @Override
    public void stopNfc() throws SharkProtocolNotSupportedException {
        currentStub.stop();
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
    public void sendKnowledge(Knowledge k, PeerSemanticTag recipient, KnowledgePort kp) throws SharkSecurityException, SharkKBException, IOException {
        if (currentStub != null && currentStub instanceof WifiDirectStreamStub) {
            WifiDirectStreamStub wifiStub = (WifiDirectStreamStub) currentStub;
            recipient.setAddresses(new String[]{wifiStub.getConnectionStr()});
        }
        super.sendKnowledge(k, recipient, kp);
    }
}
