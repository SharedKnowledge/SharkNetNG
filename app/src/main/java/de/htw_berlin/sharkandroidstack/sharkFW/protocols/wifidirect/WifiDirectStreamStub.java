package de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
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

import de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect.StubController;

/**
 * Created by micha on 28.01.16.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class WifiDirectStreamStub implements StreamStub, StubController {

    private WifiP2pDnsSdServiceRequest serviceRequest;
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

    private WifiDirectStatus statusListener;

    public WifiDirectStreamStub(Context context, WeakReference<Activity> activity) {
        this.context = context;
        this.activity = activity;

        this.manager = (WifiP2pManager) this.context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.channel = this.manager.initialize(this.context, this.context.getMainLooper(), null);

        this.txtRecordMap = new HashMap<>();
        this.txtRecordMap.put("entry0", getMACAddress());
        this.txtRecordMap.put("entry1", "This is just a test");
        this.txtRecordMap.put("entry2", "to check if discovering is working");
        this.communicationManager = CommunicationManager.getInstance();
        this.communicationManager.setStubControllerListener(this);

        this.serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_shark", "_presence._tcp", this.txtRecordMap);

        this.manager.setDnsSdResponseListeners(this.channel, null, communicationManager);
        this.serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        this.communicationManager.onStatusChanged(WifiDirectStatus.INITIATED);

        threadHandler = new Handler();
        thread = new Runnable() {
            @Override
            public void run() {
                manager.removeServiceRequest(
                        channel, serviceRequest, null);

                manager.addLocalService(channel, serviceInfo, new WifiActionListener("Add Local Service"));
                manager.addServiceRequest(
                        channel, serviceRequest, null);
                manager.discoverServices(channel, new WifiActionListener("Discover services"));
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
            this.manager.removeServiceRequest(
                    this.channel, serviceRequest, new WifiActionListener("Remove service request"));
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
        threadHandler.post(thread);
    }
}
