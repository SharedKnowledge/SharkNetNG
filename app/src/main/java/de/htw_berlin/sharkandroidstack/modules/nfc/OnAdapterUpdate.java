package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class OnAdapterUpdate {
    private final Runnable updater;
    private final WeakReference<Activity> activity;
    private final MyResultAdapter adapter;
    protected long count;
    protected int tagCount;

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

    public long readAndResetCount() {
        long countTmp = count;
        count = 0;
        return countTmp;
    }

    public int resetTagCount() {
        int tmp = tagCount;
        tagCount = 0;
        return tmp;
    }
}
