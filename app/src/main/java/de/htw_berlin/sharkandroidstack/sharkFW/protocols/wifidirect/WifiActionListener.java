package de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by micha on 28.01.16.
 */
public class WifiActionListener implements WifiP2pManager.ActionListener {

    private String message;

    public WifiActionListener(String message) {
        this.message = message;
    }

    @Override
    public void onSuccess() {
//        Log.d("WifiP2P", message + " successfull");
    }

    @Override
    public void onFailure(int reason) {
        String reasonText = "";
        switch (reason){
            case 0:
                reasonText="ERROR";
                break;
            case 1:
                reasonText="P2P_UNSUPPORTED";
                break;
            case 2:
                reasonText="BUSY";
        }
        Log.d("WifiP2P", message + " failed. Reason: " + reasonText);
    }
}
