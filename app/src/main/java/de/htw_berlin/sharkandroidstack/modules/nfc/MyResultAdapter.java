package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;
import de.htw_berlin.sharkandroidstack.Utils;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageReceived;
import de.htw_berlin.sharkandroidstack.sharkFW.protocols.nfc.OnMessageSend;

public class MyResultAdapter extends BaseAdapter implements OnMessageSend, OnMessageReceived {

    private final ArrayList<MyDataHolder> data = new ArrayList<>();

    private final LayoutInflater layoutInflater;
    private final WeakReference<Activity> activityReference;
    private int msgLength;

    public MyResultAdapter(Activity activity) {
        activityReference = new WeakReference<>(activity);
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.module_nfc_benchmark_list_entry, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        MyDataHolder current = (MyDataHolder) getItem(position);
        mViewHolder.update(current);

        return convertView;
    }

    public void addTagChanged(String message) {
        int count = data.size() + 1;
        data.add(new MyDataHolder("tag changed / " + count, message));
        notifyForUpdate();
    }

    public void addMessageIn(byte[] message) {
        int count = data.size() + 1;
        data.add(new MyDataHolder("incoming / " + count, message));
        notifyForUpdate();
    }

    public void addMessageOut(byte[] message) {
        int count = data.size() + 1;
        data.add(new MyDataHolder("outgoing / " + count, message));
        notifyForUpdate();
    }

    public void addMessageError(String message) {
        int count = data.size() + 1;
        data.add(new MyDataHolder("error / " + count, message));

        notifyForUpdate();
    }

    private void notifyForUpdate() {
        Activity activity = activityReference.get();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyResultAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onMessage(final byte[] message) {
        addMessageIn(message);
    }

    @Override
    public void onError(Exception exception) {
        exception.printStackTrace();
        addMessageError(exception.getMessage());
    }

    @Override
    public void tagLost() {
        addTagChanged("Tag lost");
    }

    @Override
    public byte[] getNextMessage() {
        byte[] message = Utils.generateRandomString(msgLength).getBytes();
        addMessageOut(message);
        return message;
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }

    private class MyViewHolder {
        private TextView stats;
        private TextView asString;
        private TextView raw;

        public MyViewHolder(View item) {
            stats = (TextView) item.findViewById(R.id.stats);
            asString = (TextView) item.findViewById(R.id.asString);
            raw = (TextView) item.findViewById(R.id.raw);
        }

        public void update(MyDataHolder data) {
            stats.setText(data.stats);

            if (data.data != null) {
                asString.setText(data.data);
            } else {
                asString.setText(new String(data.raw));
                raw.setText(Arrays.toString(data.raw));
            }
        }
    }

    private class MyDataHolder {
        String stats;
        byte[] raw;
        String data;
        // count
        // raw / info
        // direction . in, out
        // type . error, new tag, tag lost, data

        public MyDataHolder(String stats, byte[] raw) {
            this.stats = stats;
            this.raw = raw;
        }

        public MyDataHolder(String stats, String data) {
            this.stats = stats;
            this.data = data;
        }
    }
}
