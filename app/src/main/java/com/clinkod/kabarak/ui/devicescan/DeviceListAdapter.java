package com.clinkod.kabarak.ui.devicescan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clinkod.kabarak.R;
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends BaseAdapter {

    private Context context;

    private List<ScanDeviceBean> listVals;
    private List<String> deviceAddresses;

    public DeviceListAdapter(Context context, List<ScanDeviceBean> listVal){
        this.context = context;
        this.listVals = listVal;
        this.deviceAddresses = new ArrayList<>();
    }


    public void addDevice(ScanDeviceBean device){
        if(!deviceAddresses.contains(device.getDeviceMac())){
            deviceAddresses.add(device.getDeviceMac());
            listVals.add(device);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return listVals.size();
    }

    @Override
    public ScanDeviceBean getItem(int position) {
        return this.listVals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScanDeviceBean device =getItem(position);

        viewHolder.deviceName.setText(device.getDeviceName());
        viewHolder.deviceAddress.setText(device.getDeviceMac());

        return convertView;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
