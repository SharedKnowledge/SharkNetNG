package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import de.htw_berlin.sharkandroidstack.R;

public class MyKbListAdapter extends BaseAdapter {

    private final ArrayList<MyDataHolder> data = new ArrayList<>();

    private final LayoutInflater layoutInflater;

    public MyKbListAdapter(Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > data.size() || position < 0) {
            return null;
        }
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
            convertView = layoutInflater.inflate(R.layout.module_nfc_sharkdemo_list_entry, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        final Object item = getItem(position);
        if (item == null) {
            return convertView;
        }

        MyDataHolder current = (MyDataHolder) item;
        mViewHolder.update(current);

        return convertView;
    }

    public void add(MyDataHolder dataHolder) {
        data.add(dataHolder);
    }

    private class MyViewHolder {
//        final TextView stats;
//        final TextView asString;
//        final TextView raw;

        final View.OnClickListener toggleRawView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final int invertedVisibility = raw.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
//                if (raw.length() == 0 && invertedVisibility == View.VISIBLE) {
//                    return;
//                }
//
//                raw.setVisibility(invertedVisibility);
            }
        };

        public MyViewHolder(View item) {
//            stats = (TextView) item.findViewById(R.id.stats);
//            asString = (TextView) item.findViewById(R.id.asString);
//            raw = (TextView) item.findViewById(R.id.raw);
//
//            raw.setVisibility(View.GONE);
//            asString.setOnClickListener(toggleRawView);
        }

        public void update(MyDataHolder data) {
//            asString.setText("");
//            raw.setText("");
//            raw.setVisibility(View.GONE);
//
//            if (data.getRawData() != null) {
//                asString.setText(new String(data.getRawData()));
//                raw.setText(Arrays.toString(data.getRawData()));
//            } else if (data.getData() != null) {
//                asString.setText(data.getData());
//            }
        }
    }

    public static class MyDataHolder {
        public MyDataHolder(String type, String data) {

        }
    }
}
