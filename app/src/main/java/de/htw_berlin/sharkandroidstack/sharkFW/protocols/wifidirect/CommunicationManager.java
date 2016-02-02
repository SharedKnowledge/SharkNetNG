package de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect;

import android.annotation.TargetApi;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by micha on 28.01.16.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CommunicationManager implements WifiP2pManager.DnsSdTxtRecordListener, WifiDirectStreamStub.Status{

    public interface StubController{
        public void onStubStart();
        public void onStubStop();
        public void onStubRestart();
    }

    public interface WifiDirectPeerListener {
        public void onNewPeer(List<WifiDirectPeer> peers);
    }

    interface ControllerActions{
        public void onConnect(WifiDirectPeer peer);
        public void onDisconnect(WifiDirectPeer peer);
    }

    private static CommunicationManager instance = new CommunicationManager();
    private List<WifiDirectPeer> peers = new LinkedList<>();
    private ControllerActions controllerActionsListener;
    private WifiDirectPeerListener wifiDirectPeerListener;
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

    public void setWifiDirectPeerListener(WifiDirectPeerListener wifiDirectPeerListener) {
        this.wifiDirectPeerListener = wifiDirectPeerListener;
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.d("onDnsSdTxtRecordAvailable", "new entries.");
        if(srcDevice!=null && !txtRecordMap.isEmpty())
            Log.d("TxtRecods", "Records received.");
        else
            Log.d("TxtRecods", "No Records received");

        WifiDirectPeer newPeer = new WifiDirectPeer(srcDevice, txtRecordMap);
        if(!this.peers.contains(newPeer)){
            this.peers.add(newPeer);
        }
        if(!peers.isEmpty())
            wifiDirectPeerListener.onNewPeer(peers);
    }

    @Override
    public void onStatusChanged(int status) {
        switch (status){
            case WifiDirectStreamStub.DISCOVERING:
                Log.d("onStatusChanged", "DISCOVERING...");
                break;
            case WifiDirectStreamStub.CONNECTED:
                Log.d("onStatusChanged", "CONNECTED.");
                break;
            case WifiDirectStreamStub.DISCONNECTED:
                Log.d("onStatusChanged", "DISCONECTED.");
                break;
            case WifiDirectStreamStub.STOPPED:
                Log.d("onStatusChanged", "STOPPED.");
                break;
            case WifiDirectStreamStub.INITIATED:
                Log.d("onStatusChanged", "INITIATED.");
                break;
        }
    }

}
