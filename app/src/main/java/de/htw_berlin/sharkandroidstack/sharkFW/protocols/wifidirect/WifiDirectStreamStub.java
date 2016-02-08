package de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.os.Handler;

import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by micha on 28.01.16.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class WifiDirectStreamStub extends BroadcastReceiver implements StreamStub, StubController, WifiP2pManager.ConnectionInfoListener {

    private IntentFilter intentFilter;
    private Context context;
    private final WeakReference<Activity> activity;
    private RequestHandler handler;

    private boolean isStarted = false;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiP2pDnsSdServiceInfo serviceInfo;
    private Map<String, String> txtRecordMap;

    private CommunicationManager communicationManager;
    private Handler threadHandler;
    private Runnable thread;
    private int threadRuns = 0;

    public WifiDirectStreamStub(Context context, WeakReference<Activity> activity) {
        this.context = context;
        this.activity = activity;

        this.manager = (WifiP2pManager) this.context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.channel = this.manager.initialize(this.context, this.context.getMainLooper(), null);

        this.communicationManager = CommunicationManager.getInstance();
        this.communicationManager.setStubControllerListener(this);
        this.manager.setDnsSdResponseListeners(channel, null, communicationManager);
        this.communicationManager.onStatusChanged(WifiDirectStatus.INITIATED);

        this.intentFilter = new IntentFilter();
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        this.intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        this.txtRecordMap = new HashMap<>();
        this.txtRecordMap.put("entry0", getMACAddress());
        this.txtRecordMap.put("entry1", "This is just a test");
        this.txtRecordMap.put("entry2", "to check if discovering is working");
        this.serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_shark", "_presence._tcp", this.txtRecordMap);


        threadHandler = new Handler();
        thread = new Runnable() {
            @Override
            public void run() {
                removeServiceAdvertizer();
                stopServiceDiscovery();
                addServiceAdvertizer();
                startServiceDiscovery();
//                int timer = threadRuns < 3 ? 30000 - 10000*threadRuns : 10000 ;
//                threadRuns++;
                threadHandler.postDelayed(this, 10000);
            }
        };

    }

    public String getMACAddress() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("/sys/class/net/wlan0/address"));
            String address = br.readLine();
            br.close();
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public StreamConnection createStreamConnection(String addressString) throws IOException {
        return null;
    }

    @Override
    public String getLocalAddress() {
        return getMACAddress();
    }

    @Override
    public void setHandler(RequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public void stop() {
        if(isStarted){
            threadHandler.removeCallbacks(thread);
            stopServiceDiscovery();
            removeServiceAdvertizer();
            communicationManager.onStatusChanged(WifiDirectStatus.STOPPED);
            isStarted=!isStarted;
        }
    }

    @Override
    public void start() throws IOException {
        if(!isStarted){
            threadHandler.post(thread);
            communicationManager.onStatusChanged(WifiDirectStatus.DISCOVERING);
            isStarted=!isStarted;
        }
    }

    private void startPeerDiscovering(){
        this.context.registerReceiver(this, this.intentFilter);
        this.manager.discoverPeers(this.channel, new WifiActionListener("Discover Peers"));
    }

    private void stopPeerDiscovering(){
        this.manager.discoverPeers(this.channel, new WifiActionListener("Stop Peerdiscovering"));
        this.context.unregisterReceiver(this);
    }

    private void addServiceAdvertizer(){
        manager.addLocalService(channel, serviceInfo, new WifiActionListener("Add LocalService"));
    }

    private void removeServiceAdvertizer(){
        manager.clearLocalServices(channel, new WifiActionListener("Clear LocalServices"));
    }

    private void startServiceDiscovery(){
        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest, new WifiActionListener("Add ServiceRequest"));
        manager.discoverServices(channel, new WifiActionListener("Discover services"));
    }

    private void stopServiceDiscovery(){
        manager.clearServiceRequests(channel, new WifiActionListener("Clear ServiceRequests"));
    }

    @Override
    public boolean started() {
        return isStarted;
    }

    @Override
    public void onStubStart() throws IOException {
        if(!isStarted)
            start();
    }

    @Override
    public void onStubStop() {
        if(isStarted)
            stop();
    }

    @Override
    public void onStubRestart() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (manager != null) {
                manager.requestPeers(channel, communicationManager);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }
}
