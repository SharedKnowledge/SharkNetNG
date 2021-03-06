package de.htw_berlin.sharkandroidstack.modules.nfc.benchmark;

import android.app.Activity;
import android.nfc.cardemulation.HostApduService;

import de.htw_berlin.sharkandroidstack.AndroidUtils;
import de.htw_berlin.sharkandroidstack.modules.nfc.benchmark.MyResultAdapter.MyDataHolder;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

/**
 * Created by mn-io on 22.01.16.
 */
public class OnMessageSendImpl extends OnAdapterUpdate implements OnMessageSend {

    private int size;

    public OnMessageSendImpl(MyResultAdapter adapter, Runnable updater, Activity activity) {
        super(adapter, activity, updater);
    }

    @Override
    public byte[] getNextMessage() {
        byte[] message = AndroidUtils.generateRandomString(size).getBytes();

        startTimer();
        countByte += message.length;
        countMsg++;

        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_OUT, MyDataHolder.TYPE_DATA, message);
        update(dataHolder);
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

        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_OUT, MyDataHolder.TYPE_LOST_TAG, r);
        update(dataHolder);
    }

    @Override
    public void setMaxSize(int size) {
        this.size = size;
    }
}

