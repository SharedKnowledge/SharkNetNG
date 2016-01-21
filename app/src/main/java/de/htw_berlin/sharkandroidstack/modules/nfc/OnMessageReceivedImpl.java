package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;
import android.nfc.Tag;

import de.htw_berlin.sharkandroidstack.modules.nfc.MyResultAdapter.MyDataHolder;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;

public class OnMessageReceivedImpl extends OnAdapterUpdate implements OnMessageReceived {
    public OnMessageReceivedImpl(MyResultAdapter adapter, Runnable updater, Activity activity) {
        super(adapter, activity, updater);
    }

    @Override
    public void onMessage(byte[] message) {
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_DATA, message);
        countData(message);
        update(dataHolder);
    }

    @Override
    public void onError(Exception exception) {
        exception.printStackTrace();
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_ERROR, exception.getMessage());
        update(dataHolder);
    }

    @Override
    public void tagLost(Tag tag) {
        String data = tag != null ? tag.toString() : null;
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_LOST_TAG, data);
        update(dataHolder);
    }

    @Override
    public void newTag(Tag tag) {
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_NEW_TAG, tag.toString());
        update(dataHolder);
    }
}
