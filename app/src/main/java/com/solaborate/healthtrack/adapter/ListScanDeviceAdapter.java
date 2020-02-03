package com.solaborate.healthtrack.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.solaborate.healthtrack.R;
import com.solaborate.healthtrack.model.DeviceCharacteristic;

import java.util.ArrayList;
import java.util.List;

public class ListScanDeviceAdapter extends BaseAdapter {
    private Context mContext;
    private List<DeviceCharacteristic> mList = new ArrayList<>();

    public ListScanDeviceAdapter(Context context, List<DeviceCharacteristic> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void setList(List<DeviceCharacteristic> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {

        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        viewHolder viewHolder;
        if (view == null) {
            viewHolder = new viewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_scan_device, null);
            viewHolder.tvMac = (TextView) view.findViewById(R.id.tvMac);
            viewHolder.tvBtn = (TextView) view.findViewById(R.id.tvBtn);
            view.setTag(viewHolder);
        } else {
            viewHolder = (viewHolder) view.getTag();
        }
        DeviceCharacteristic bean = mList.get(i);
        viewHolder.tvMac.setText(bean.getDeviceMac());
        return view;
    }

    class viewHolder {
        public TextView tvMac;
        public TextView tvBtn;
    }
}
