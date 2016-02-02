package de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect;

import android.annotation.TargetApi;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by micha on 28.01.16.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CommunicationManager implements WifiP2pManager.DnsSdTxtRecordListener, WifiDirectStreamStub.Status{

    interface StubController{
        public void onStubStart();
        public void onStubStop();
        public void onStubRestart();
    }

    interface ControllerActions{
        public void onNewPeer(List<WifiDirectPeer> peers);
        public void onConnect(WifiDirectPeer peer);
        public void onDisconnect(WifiDirectPeer peer);
    }

    private static CommunicationManager instance = new CommunicationManager();
    private List<WifiDirectPeer> peers = new LinkedList<>();
    private ControllerActions controllerActionsListener;
    private StubController stubControllerListener;

    public CommunicationManager() {}

    public static CommunicationManager getInstance(){
        return instance;
    }

    public void setControllerActionsListener(ControllerActions controllerActionsListener) {
        this.controllerActionsListener = controllerActionsListener;
    }

    public void setStubControllerListener(StubController stubControllerListener) {
        this.stubControllerListener = stubControllerListener;
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        WifiDirectPeer newPeer = new WifiDirectPeer(srcDevice, txtRecordMap);
        if(!this.peers.contains(newPeer)){
            this.peers.add(newPeer);
        }
        controllerActionsListener.onNewPeer(peers);
    }

    @Override
    public void onStatusChanged(int status) {

    }

}
