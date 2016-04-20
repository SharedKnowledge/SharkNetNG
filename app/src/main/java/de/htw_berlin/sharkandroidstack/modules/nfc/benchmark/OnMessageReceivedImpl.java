package de.htw_berlin.sharkandroidstack.modules.nfc.benchmark;

import android.app.Activity;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import net.sharksystem.android.protocols.nfc.OnMessageReceived;

import de.htw_berlin.sharkandroidstack.modules.nfc.benchmark.MyResultAdapter.MyDataHolder;

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
    public void handleMessageReceived(byte[] msg) {
        startTimer();
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_DATA, msg);
        if (msg != null) {
            countByte += msg.length;
        }
        countMsg++;
        update(dataHolder);
    }

    @Override
    public void handleError(Exception exception) {
        exception.printStackTrace();
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_ERROR, exception.getMessage());
        update(dataHolder);
    }

    @Override
    public void handleTagLost() {
        MyDataHolder dataHolder = new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_LOST_TAG, "");
        update(dataHolder);
    }

    @Override
    public void handleNewTag(Tag tag) {
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
