package de.htw_berlin.sharkandroidstack.modules.wifidirect;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import net.sharkfw.kep.SharkProtocolNotSupportedException;

import java.io.IOException;
import java.util.List;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.sharkFW.peer.AndroidSharkEngine;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.CommunicationManager;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.WifiDirectPeer;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.WifiDirectPeerListener;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.WifiDirectStatus;

public class WifiDirectListActivity extends ParentActivity implements WifiDirectPeerListener{

    private ListView list;
    private AndroidSharkEngine engine;
    private WifiDirectPeerAdapter peerAdapter;
    private CommunicationManager communicationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResource(R.layout.module_wifi_direct_list_activity);
        setOptionsMenu(R.menu.module_wifidirectlist_menu);

        this.communicationManager = CommunicationManager.getInstance();
        this.communicationManager.setWifiDirectPeerListener(this);
        this.communicationManager.setContext(this);

        list = (ListView) findViewById(R.id.wifidirectListView);
        peerAdapter = new WifiDirectPeerAdapter(this);
        list.setAdapter(peerAdapter);

        engine = new AndroidSharkEngine(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.wifidirect_menu_start:
                this.communicationManager.startStub();
                break;
            case R.id.wifidirect_menu_stop:
                this.communicationManager.stopStub();
                break;
            case R.id.wifidirect_menu_refresh:
                this.communicationManager.restartStub();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            this.engine.stopWifiDirect();
        } catch (SharkProtocolNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            engine.startWifiDirect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SharkProtocolNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public boolean isWifiEnabled(){
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null) {
            return wifiManager.isWifiEnabled();
        }else{
            return false;
        }
    }

    public boolean setWifiEnabled(boolean enabled){
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null) {
            return wifiManager.setWifiEnabled(enabled);
        }else{
            return false;
        }
    }

    @Override
    public void onNewPeer(List<WifiDirectPeer> peers) {
        Log.d("LIST", "New Peers found: " + peers.size());
        peerAdapter.setList(peers);
    }
}
