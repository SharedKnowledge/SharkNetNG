package de.htw_berlin.sharkandroidstack.sharkFW.protocols.wifidirect;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;

import net.sharkfw.kep.KEPStub;
import net.sharkfw.protocols.RequestHandler;
import net.sharkfw.protocols.StreamConnection;
import net.sharkfw.protocols.StreamStub;
import net.sharkfw.protocols.Stub;

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
public class WifiDirectStreamStub implements StreamStub, WifiP2pManager.DnsSdTxtRecordListener {

    private WifiP2pDnsSdServiceRequest serviceRequest;
    private Context context;
    private final WeakReference<Activity> activity;
    private RequestHandler handler;
    private boolean isStarted = false;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Map<String, String> txtRecordMap;

    public WifiDirectStreamStub(Context context, WeakReference<Activity> activity) {
        this.context = context;
        this.activity = activity;

        this.manager = (WifiP2pManager) this.context.getSystemService(Context.WIFI_P2P_SERVICE);
        this.channel = this.manager.initialize(this.context, this.context.getMainLooper(), null);

        this.txtRecordMap = new HashMap<>();
        this.txtRecordMap.put("entry0", getMACAddress());
        this.txtRecordMap.put("entry1", "This is just a test");
        this.txtRecordMap.put("entry2", "to check if discovering is working");

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_shark", "_presence._tcp", this.txtRecordMap);

        this.manager.addLocalService(this.channel, serviceInfo, new WifiActionListener("Add Local Service"));

        // TODO Set TxtListener to CommunicationManager
        this.manager.setDnsSdResponseListeners(this.channel, null, this);
        this.serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
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
        this.manager.removeServiceRequest(
                this.channel, serviceRequest, new WifiActionListener("Remove service request"));
    }

    @Override
    public void start() throws IOException {
        this.manager.addServiceRequest(
                this.channel, serviceRequest, new WifiActionListener("Add service request"));
        this.manager.discoverServices(this.channel, new WifiActionListener("Discover services"));
    }

    @Override
    public boolean started() {
        return isStarted;
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {

    }
}
