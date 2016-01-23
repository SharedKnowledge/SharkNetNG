package de.htw_berlin.sharkandroidstack.modules.nfc.sharkdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.htw_berlin.sharkandroidstack.R;

/**
 * Created by mn-io on 22.01.16.
 */
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
        notifyDataSetChanged();
    }

    private class MyViewHolder {
        final TextView headerField;
        final TextView dataField;

        public MyViewHolder(View item) {
            headerField = (TextView) item.findViewById(R.id.header);
            dataField = (TextView) item.findViewById(R.id.data);
        }

        public void update(MyDataHolder data) {
            headerField.setText(data.type);
            dataField.setText(data.data);
        }
    }

    public static class MyDataHolder {
        final String type;
        final String data;

        public MyDataHolder(String type, String data) {
            this.type = type;
            this.data = data;
        }
    }
}
