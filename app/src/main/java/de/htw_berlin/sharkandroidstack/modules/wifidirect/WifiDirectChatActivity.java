package de.htw_berlin.sharkandroidstack.modules.wifidirect;

import android.os.Bundle;
import android.view.MenuItem;

import net.sharksystem.android.protocols.wifidirect.SharkWifiDirectManager;
import net.sharksystem.android.protocols.wifidirect.WifiDirectPeer;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.android.ParentActivity;

public class WifiDirectChatActivity extends ParentActivity {

    private SharkWifiDirectManager _sharkWifiDirectManager;
    private WifiDirectPeer _peer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _sharkWifiDirectManager = SharkWifiDirectManager.getInstance(this);
        _peer = _sharkWifiDirectManager.getConnectedPeer();

        setTitle(_peer.getName());
        setLayoutResource(R.layout.activity_wifi_direct_chat);
        setOptionsMenu(R.menu.module_wifi_direct_chat_menu);

        _sharkWifiDirectManager.connect(_peer);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.wifidirect_menu_back){
            _sharkWifiDirectManager.disconnect(_peer);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
