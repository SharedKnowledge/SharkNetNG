package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;
import android.nfc.cardemulation.HostApduService;

import de.htw_berlin.sharkandroidstack.Utils;
import de.htw_berlin.sharkandroidstack.modules.nfc.MyResultAdapter.MyDataHolder;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

public class OnMessageSendImpl extends OnAdapterUpdate implements OnMessageSend {

    private int msgLength;

    public OnMessageSendImpl(MyResultAdapter adapter, Runnable updater, Activity activity) {
        super(adapter, activity, updater);
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }

    @Override
    public byte[] getNextMessage() {
        byte[] message = Utils.generateRandomString(msgLength).getBytes();

        startTimer();
        count += message.length;

        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_OUT, MyDataHolder.TYPE_DATA, message);
        update(dataHolder);
        return message;
    }

    @Override
    public void onDeactivated(int reason) {
        tagCount++;
        String r = "";
        switch (reason) {
            case HostApduService.DEACTIVATION_DESELECTED:
                r = "deselected";
                break;
            case HostApduService.DEACTIVATION_LINK_LOSS:
                r = "link loss";
                break;
        }

        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_OUT, MyDataHolder.TYPE_LOST_TAG, r);
        update(dataHolder);
    }
}

