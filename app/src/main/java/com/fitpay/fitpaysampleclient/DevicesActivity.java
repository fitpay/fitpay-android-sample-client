package com.fitpay.fitpaysampleclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.fitpaysampleclient.adapters.DevicesAdapter;

import java.util.ArrayList;
import java.util.List;

public class DevicesActivity extends AppCompatActivity {

    private User user;
    private List<Device> collection;
    private View progress;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        progress = findViewById(R.id.progress_devices);
        listView = (ListView) findViewById(R.id.list);
        user = getIntent().getParcelableExtra("user");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDevice(position);
            }
        });
        getDevices();
    }

    private void getDevices() {
        showProgress(true);
        user.getDevices(10, 0, new ApiCallback<Collections.DeviceCollection>() {
            @Override
            public void onSuccess(Collections.DeviceCollection result) {
                collection = result.getResults();
                updateViews();
                showProgress(false);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
                //todo show error
            }
        });

    }


    private void updateViews() {
        if (collection == null) {
            List<Device> testDevices = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Device testDevice = new Device.Builder().setDeviceName("device " + i).create();
                testDevices.add(testDevice);
            }

            collection = testDevices;
        }
        DevicesAdapter mAdapter = new DevicesAdapter(this, R.layout.device_item, collection);
        listView.setAdapter(mAdapter);
    }

    private void goToDevice(int position) {
        Device device = collection.get(position);
        Intent intent = new Intent(DevicesActivity.this, DeviceActivity.class);
        intent.putExtra("device", device);
        startActivity(intent);
    }

    private void showProgress(boolean value) {
        listView.setVisibility(value ? View.GONE : View.VISIBLE);
        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }


}
