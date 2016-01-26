package de.htw_berlin.sharkandroidstack.modules.nfc.benchmark;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Created by mn-io on 22.01.16.
 */
public class OnAdapterUpdate {
    private final Runnable updater;
    private final WeakReference<Activity> activity;
    private final MyResultAdapter adapter;
    protected long countByte = 0;
    protected int countMsg = 0;
    protected int tagCount = 0;
    private long startTime = 0;

    public OnAdapterUpdate(MyResultAdapter adapter, Activity activity, Runnable updater) {
        this.adapter = adapter;
        this.updater = updater;
        this.activity = new WeakReference<>(activity);
    }

    protected void update(final MyResultAdapter.MyDataHolder dataHolder) {
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
        if (startTime == 0) {
            return -1;
        }
        long stopTime = System.currentTimeMillis();
        final long diff = stopTime - startTime;
        startTime = 0;
        return diff;
    }

    public long readAndResetByteCount() {
        long countTmp = countByte;
        countByte = 0;
        return countTmp;
    }

    public int readAndResetTagCount() {
        int tmp = tagCount;
        tagCount = 0;
        return tmp;
    }

    public int readAndResetMsgCount() {
        int tmp = countMsg;
        countMsg = 0;
        return tmp;
    }
}
