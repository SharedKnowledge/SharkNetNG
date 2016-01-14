package de.htw_berlin.sharkandroidstack.modules.nfc;

import de.htw_berlin.sharkandroidstack.Utils;
import de.htw_berlin.sharkandroidstack.modules.nfc.MyResultAdapter.MyDataHolder;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

public class OnMessageSendImpl implements OnMessageSend {
    private MyResultAdapter adapter;
    private Runnable updater;
    private int msgLength;

    public OnMessageSendImpl(MyResultAdapter adapter, Runnable updater) {
        this.adapter = adapter;
        this.updater = updater;
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

    private void update() {
        updater.run();
    }
}

