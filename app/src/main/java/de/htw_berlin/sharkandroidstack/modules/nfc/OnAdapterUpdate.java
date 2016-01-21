package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class OnAdapterUpdate {
    private final Runnable updater;
    private final WeakReference<Activity> activity;
    private final MyResultAdapter adapter;
    protected long count = 0;
    protected int tagCount = 0;
    private long startTime = 0;

    public OnAdapterUpdate(MyResultAdapter adapter, Activity activity, Runnable updater) {
        this.adapter = adapter;
        this.updater = updater;
        this.activity = new WeakReference<>(activity);
    }

    protected void update(final MyResultAdapter.MyDataHolder dataHolder) {
        //ugly and expensive, but needed to avoid exception:
        // "IllegalStateException: The content of the adapter has changed but ListView did not receive a notification."
        // need to update adapter on UI thread with current data
        activity.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(dataHolder);
                adapter.notifyDataSetChanged();
                updater.run();
            }
        });
    }

    public void startTimer() {
        if (startTime != 0) {
            return;
        }
        startTime = System.currentTimeMillis();
    }

    public long readAndResetTimer() {
        long stopTime = System.currentTimeMillis();
        final long diff = stopTime - startTime;
        startTime = 0;
        return diff;
    }

    public long readAndResetCount() {
        long countTmp = count;
        count = 0;
        return countTmp;
    }

    public int readAndResetTagCount() {
        int tmp = tagCount;
        tagCount = 0;
        return tmp;
    }
}
