package de.htw_berlin.sharkandroidstack.modules.wifidirect;

import android.os.Bundle;
import android.view.MenuItem;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.CommunicationManager;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.WifiDirectPeer;

public class WifiDirectChatActivity extends ParentActivity {

    private CommunicationManager _communicationManager;
    private WifiDirectPeer _peer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _communicationManager = CommunicationManager.getInstance();
        _peer = _communicationManager.getConnectedPeer();

        setTitle(_peer.getName());
        setLayoutResource(R.layout.activity_wifi_direct_chat);
        setOptionsMenu(R.menu.module_wifi_direct_chat_menu);

        _communicationManager.connect(_peer);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.wifidirect_menu_back){
            _communicationManager.disconnect(_peer);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
