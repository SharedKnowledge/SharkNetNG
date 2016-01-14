package de.htw_berlin.sharkandroidstack.modules.nfc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;

public class MyResultAdapter extends BaseAdapter {

    private final ArrayList<MyDataHolder> data = new ArrayList<>();

    private final LayoutInflater layoutInflater;

    public MyResultAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public void add(MyDataHolder dataHolder) {
        dataHolder.setCount(data.size() + 1);
        data.add(dataHolder);
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
            String direction = data.getDirection() > 0 ? "incoming" : "outgoing";
            String type = "Error";
            switch (data.getType()) {
                case MyDataHolder.TYPE_DATA:
                    type = "data";
                    break;
                case MyDataHolder.TYPE_NEW_TAG:
                    type = "new tag";
                    break;
                case MyDataHolder.TYPE_LOST_TAG:
                    type = "lost tag";
                    break;
            }

            stats.setText(data.getCount() + " / " + direction + " / " + type);

            asString.setText("");
            raw.setText("");

            if (data.getRawData() != null) {
                asString.setText(new String(data.getRawData()));
                raw.setText(Arrays.toString(data.getRawData()));
            } else if (data.getData() != null) {
                asString.setText(data.getData());
            }
        }
    }

    public static class MyDataHolder {
        public static final int TYPE_ERROR = 1;
        public static final int TYPE_NEW_TAG = 2;
        public static final int TYPE_LOST_TAG = 3;
        public static final int TYPE_DATA = 4;

        public static final int DIRECTION_IN = 1;
        public static final int DIRECTION_OUT = -1;

        private int count;
        private int direction;
        private int type;
        private byte[] rawData;
        private String data;

        public MyDataHolder(int direction, int type, byte[] rawData) {
            this.setDirection(direction);
            this.setType(type);
            this.rawData = rawData;
        }

        public MyDataHolder(int direction, int type, String data) {
            this.setDirection(direction);
            this.setType(type);
            this.data = data;
        }

        public MyDataHolder(int count, int direction, int type) {
            this.count = count;
            this.setDirection(direction);
            this.setType(type);
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

        public int getDirection() {
            return direction;
        }

        private void setDirection(int direction) {
            if (direction != DIRECTION_IN && direction != DIRECTION_OUT) {
                throw new IllegalArgumentException("Invalid direction value: " + direction);
            }
            this.direction = direction;
        }

        public int getType() {
            return type;
        }

        private void setType(int type) {
            if (type != TYPE_ERROR && type != TYPE_NEW_TAG && type != TYPE_LOST_TAG && type != TYPE_DATA) {
                throw new IllegalArgumentException("Invalid type value: " + type);
            }
            this.type = type;
        }

        public byte[] getRawData() {
            return rawData;
        }

        public String getData() {
            return data;
        }
    }
}
