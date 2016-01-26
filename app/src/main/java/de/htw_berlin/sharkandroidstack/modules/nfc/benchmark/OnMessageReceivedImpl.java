package de.htw_berlin.sharkandroidstack.modules.nfc.benchmark;

import android.app.Activity;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import de.htw_berlin.sharkandroidstack.modules.nfc.benchmark.MyResultAdapter.MyDataHolder;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;

/**
 * Created by mn-io on 22.01.16.
 */
public class OnMessageReceivedImpl extends OnAdapterUpdate implements OnMessageReceived {

    private int maxMsgSize;
    private int tagCount = 0;

    public OnMessageReceivedImpl(MyResultAdapter adapter, Runnable updater, Activity activity) {
        super(adapter, activity, updater);
    }

    @Override
    public void onMessage(byte[] message) {
        startTimer();
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_DATA, message);
        if (message != null) {
            countByte += message.length;
        }
        countMsg++;
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
        startTimer();
        tagCount++;
        maxMsgSize = IsoDep.get(tag).getMaxTransceiveLength();
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_NEW_TAG, tag.toString());
        update(dataHolder);
    }

    public int readAndResetMsgSize() {
        int tmp = maxMsgSize;
        maxMsgSize = 0;
        return tmp;
    }

    public int readAndResetTagCount() {
        int tmp = tagCount;
        tagCount = 0;
        return tmp;
    }

}
