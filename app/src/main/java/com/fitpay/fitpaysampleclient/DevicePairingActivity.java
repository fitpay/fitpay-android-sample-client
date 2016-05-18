package com.fitpay.fitpaysampleclient;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.DeviceService;
import com.fitpay.android.paymentdevice.impl.ble.BluetoothPaymentDeviceService;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DevicePairingActivity extends AppCompatActivity {

    //TODO what the heck is this?
    private static final int SELECT_DEVICE = 1010;

    @Bind(R.id.progress)
    View progress;

    @Bind(R.id.btn_find_ble)
    Button btnFindBle;

    @Bind(R.id.btn_use_mock_device)
    Button btnUseMock;

    @Bind(R.id.btn_use_pebble_pagare)
    Button btnUsePebblePagare;


    private BluetoothDevice bluetoothDevice;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pairing);
        ButterKnife.bind(this);

        btnFindBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectDevice = new Intent(DevicePairingActivity.this, SelectDeviceActivity.class);
                startActivityForResult(selectDevice, SELECT_DEVICE);
            }
        });

        btnUseMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deviceServiceIntent = new Intent(DevicePairingActivity.this, DeviceService.class);
                deviceServiceIntent.putExtra(DeviceService.EXTRA_PAYMENT_SERVICE_TYPE, DeviceService.PAYMENT_SERVICE_TYPE_MOCK);
                startService(deviceServiceIntent);
                gotoCreateDevice();
            }
        });

        btnUsePebblePagare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deviceServiceIntent = new Intent(DevicePairingActivity.this, DeviceService.class);
                deviceServiceIntent.putExtra(DeviceService.EXTRA_PAYMENT_SERVICE_TYPE, "com.fitpay.android.paymentdevice.impl.pagare.PebblePagarePaymentServiceDevice");
                deviceServiceIntent.putExtra(DeviceService.EXTRA_PAYMENT_SERVICE_CONFIG, "PEBBLE_APP_UUID=8c570c67-13cc-4778-a15b-5fade088e269");
                startService(deviceServiceIntent);
                gotoCreateDevice();
            }
        });

        user = (User) SessionStorage.getInstance().getData("user");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK && data != null) {
            bluetoothDevice = (BluetoothDevice) data.getExtras().get("device");
            if (bluetoothDevice != null) {
                bluetoothDevice.getAddress();
                Intent deviceServiceIntent = new Intent(DevicePairingActivity.this, DeviceService.class);
                deviceServiceIntent.putExtra(DeviceService.EXTRA_PAYMENT_SERVICE_TYPE, DeviceService.PAYMENT_SERVICE_TYPE_FITPAY_BLE);
                if (null != bluetoothDevice.getAddress()) {
                    deviceServiceIntent.putExtra(BluetoothPaymentDeviceService.EXTRA_BLUETOOTH_ADDRESS, bluetoothDevice.getAddress());
                }
                startService(deviceServiceIntent);
                gotoCreateDevice();
            } else {
                Toast.makeText(DevicePairingActivity.this, "Can not pair.  No bluetooth device selected.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void gotoCreateDevice() {
        Intent intent = new Intent(this, DeviceCreateActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);

    }


}
