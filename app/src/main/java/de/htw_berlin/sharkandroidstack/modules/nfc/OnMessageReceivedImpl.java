package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;
import android.nfc.Tag;

import java.lang.ref.WeakReference;

import de.htw_berlin.sharkandroidstack.modules.nfc.MyResultAdapter.MyDataHolder;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;

public class OnMessageReceivedImpl implements OnMessageReceived {
    private MyResultAdapter adapter;
    private Runnable updater;
    private WeakReference<Activity> activity;

    public OnMessageReceivedImpl(MyResultAdapter adapter, Runnable updater, Activity activity) {
        this.adapter = adapter;
        this.updater = updater;
        this.activity = new WeakReference<>(activity);
    }


    @Override
    public void onMessage(byte[] message) {
        adapter.add(new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_DATA, message));
        update();
    }

    @Override
    public void onError(Exception exception) {
        exception.printStackTrace();
        adapter.add(new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_ERROR, exception.getMessage()));
        update();
    }

    @Override
    public void tagLost(Tag tag) {
        String data = tag != null ? tag.toString() : null;
        adapter.add(new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_LOST_TAG, data));
        update();
    }

    @Override
    public void newTag(Tag tag) {
        adapter.add(new MyDataHolder(MyDataHolder.DIRECTION_IN, MyDataHolder.TYPE_NEW_TAG, tag.toString()));
        update();
    }

    private void update() {
        activity.get().runOnUiThread(updater);
    }
}
