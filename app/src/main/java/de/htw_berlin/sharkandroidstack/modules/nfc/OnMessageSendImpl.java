package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;
import android.nfc.cardemulation.HostApduService;

import java.lang.ref.WeakReference;

import de.htw_berlin.sharkandroidstack.Utils;
import de.htw_berlin.sharkandroidstack.modules.nfc.MyResultAdapter.MyDataHolder;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

public class OnMessageSendImpl implements OnMessageSend {
    private MyResultAdapter adapter;
    private Runnable updater;
    private WeakReference<Activity> activity;
    private int msgLength;

    public OnMessageSendImpl(MyResultAdapter adapter, Runnable updater, Activity activity) {
        this.adapter = adapter;
        this.updater = updater;
        this.activity = new WeakReference<>(activity);
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }

    @Override
    public byte[] getNextMessage() {
        byte[] message = Utils.generateRandomString(msgLength).getBytes();
        adapter.add(new MyDataHolder(MyDataHolder.DIRECTION_OUT, MyDataHolder.TYPE_DATA, message));
        update();
        return message;
    }

    @Override
    public void onDeactivated(int reason) {
        String r = "";
        switch (reason) {
            case HostApduService.DEACTIVATION_DESELECTED:
                r = "deselected";
                break;
            case HostApduService.DEACTIVATION_LINK_LOSS:
                r = "link loss";
                break;
        }
        adapter.add(new MyDataHolder(MyDataHolder.DIRECTION_OUT, MyDataHolder.TYPE_LOST_TAG, r));
        update();

    }

    private void update() {
        activity.get().runOnUiThread(updater);
    }
}

