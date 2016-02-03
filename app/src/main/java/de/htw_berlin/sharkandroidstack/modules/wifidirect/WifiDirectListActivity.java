package de.htw_berlin.sharkandroidstack.modules.wifidirect;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import net.sharkfw.kep.SharkProtocolNotSupportedException;

import java.io.IOException;
import java.util.List;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.sharkFW.peer.AndroidSharkEngine;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.CommunicationManager;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.WifiDirectPeer;

public class WifiDirectListActivity extends ParentActivity implements CommunicationManager.WifiDirectPeerListener{

    private ListView list;
    private AndroidSharkEngine engine;
    private WifiDirectPeerAdapter peerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResource(R.layout.module_wifi_direct_list_activity);

        list = (ListView) findViewById(R.id.wifidirectListView);

        CommunicationManager.getInstance().setWifiDirectPeerListener(this);

        peerAdapter = new WifiDirectPeerAdapter(this);
        list.setAdapter(peerAdapter);

        engine = new AndroidSharkEngine(this);
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

    @Override
    public void onNewPeer(List<WifiDirectPeer> peers) {
        Log.d("LIST", "New Peers found: " + peers.size());
        peerAdapter.setList(peers);
    }
}
