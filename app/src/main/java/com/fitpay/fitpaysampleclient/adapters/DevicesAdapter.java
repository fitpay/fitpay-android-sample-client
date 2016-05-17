package com.fitpay.fitpaysampleclient.adapters;
/*
 * Created by andrews on 23.03.16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.utils.DevicePreferenceData;
import com.fitpay.fitpaysampleclient.R;

import java.util.List;


public class DevicesAdapter extends ArrayAdapter<Device> {

    private final int layoutId;
    private final LayoutInflater layoutInflater;
    //private final List<Device> devices;

    public DevicesAdapter(Context context, int resource, List<Device> objects) {
        super(context, resource, objects);
        layoutId = resource;
        //devices = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Device device = getItem(position);
        ViewHolder holder;

        if (null == convertView) {
            convertView = layoutInflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.deviceName = (TextView) convertView.findViewById(R.id.item_name);
            holder.deviceId = (TextView) convertView.findViewById(R.id.item_number);
            holder.description = (TextView) convertView.findViewById(R.id.item_description);
            holder.serialNumber = (TextView) convertView.findViewById(R.id.item_serial_number);
            holder.pairedStatus = (TextView) convertView.findViewById(R.id.item_paired_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deviceName.setText(device.getDeviceName());
        holder.deviceId.setText(device.getDeviceIdentifier());
        holder.description.setText(device.getManufacturerName() + " " + device.getModelNumber());
        holder.serialNumber.setText(device.getSerialNumber());
        DevicePreferenceData data = DevicePreferenceData.loadFromPreferences(this.getContext(), device.getDeviceIdentifier());
        String pairDescription = "Not Paired";
        if (null != data && null != data.getPaymentDeviceServiceType()) {
            pairDescription = "Paired " + data.getPaymentDeviceServiceType();
        }
        holder.pairedStatus.setText(pairDescription);

        return convertView;
    }

    protected void getDevicePreferences(String deviceId) {
        getContext().getSharedPreferences("paymentDevice_" + deviceId, Context.MODE_PRIVATE);


    }

    private static class ViewHolder {
        TextView deviceName;
        TextView deviceId;
        TextView description;
        TextView serialNumber;
        TextView pairedStatus;
    }

}