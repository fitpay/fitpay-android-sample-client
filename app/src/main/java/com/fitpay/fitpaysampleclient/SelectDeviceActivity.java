package com.fitpay.fitpaysampleclient;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fitpay.android.paymentdevice.impl.ble.activities.BaseSearchBLEActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by Vlad on 23.03.2016.
 */
public class SelectDeviceActivity extends BaseSearchBLEActivity {

    private static final String DEVICE_NAME = "FitPayPD";

    ArrayList<BluetoothDevice> devices;
    DeviceAdapter adapter;
    ListView listView;

    @Override
    public void initViews() {
        setContentView(R.layout.select_device);
        listView = (ListView) findViewById(R.id.list);

        devices = new ArrayList<>();
        adapter = new DeviceAdapter(this, android.R.layout.simple_list_item_1, devices);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice d = adapter.getItem(position);

                Intent intent = new Intent();
                intent.putExtra("device", d);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onNewDevice(final BluetoothDevice device) {
        devices.add(device);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchBegin() {
        Logger.i("Search started");
    }

    @Override
    public void onSearchEnd() {

        Logger.i("Search ended");

        BluetoothDevice device = null;

        for (BluetoothDevice d : mDevicesList) {
            if (DEVICE_NAME.equals(d.getName())) {
                device = d;
                break;
            }
        }

        if(device == null){
            Logger.e("Please run FitPayPD simulator");
        }
    }

    class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {

        public DeviceAdapter(Context context, int textViewResourceId, ArrayList<BluetoothDevice> devices){
            super(context, textViewResourceId, devices);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BluetoothDevice device = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            text.setText(device.getName() + " " + device.getAddress());
            return convertView;
        }
    }
}
