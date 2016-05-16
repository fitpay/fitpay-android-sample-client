package com.fitpay.fitpaysampleclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.DeviceService;
import com.fitpay.android.paymentdevice.callbacks.PaymentDeviceListener;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.utils.DevicePreferenceData;
import com.fitpay.android.utils.NotificationManager;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DeviceCreateActivity extends AppCompatActivity {

    private final static String TAG = DeviceCreateActivity.class.getSimpleName();

    private User user;
    private Device device;
    private DeviceService deviceService;

    @Bind(R.id.status)
    TextView status;

    @Bind(R.id.device_name)
    TextView deviceName;
    @Bind(R.id.manufacturer_name)
    TextView manufacturerName;
    @Bind(R.id.model_number)
    TextView modelNumber;
    @Bind(R.id.serial_number)
    TextView serialNumber;
    @Bind(R.id.firmware_number)
    TextView firmwareNumber;
    @Bind(R.id.hardware_revision)
    TextView hardwareRevision;
    @Bind(R.id.software_revision)
    TextView softwareRevision;
    @Bind(R.id.system_id)
    TextView systemId;

    @Bind(R.id.progress)
    View progress;

    @Bind(R.id.btn_add)
    Button addBtn;
    @Bind(R.id.btn_cancel)
    Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_create);
        ButterKnife.bind(this);

        status.setText("Payment device service disconnected");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDevice();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        user = getIntent().getParcelableExtra("user");

        Intent deviceServiceIntent = new Intent(this, DeviceService.class);
        bindService(deviceServiceIntent, paymentServiceConnection, BIND_AUTO_CREATE);

        NotificationManager.getInstance().addListener(notificationListener);

        showProgress(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != deviceService && null != paymentServiceConnection) {
            deviceService.unbindService(paymentServiceConnection);
        }
    }

    private void addDevice() {
        showProgress(true);
        if (null == device) {
            showProgress(false);
            return;
        }
        user.createDevice(device, new ApiCallback<Device>() {
            @Override
            public void onSuccess(Device result) {
                device = result;
                storeDevicePreferences(device);
                showProgress(false);
                //todo show success
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Toast.makeText(DeviceCreateActivity.this, "Device creation was not successful.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private void storeDevicePreferences(Device device) {
        DevicePreferenceData data = new DevicePreferenceData.Builder()
                .deviceId(device.getDeviceIdentifier())
                .paymentDeviceServiceType(deviceService.getPaymentServiceType())
                .build();
        DevicePreferenceData.storePreferences(this, data);
    }

    private void showProgress(boolean value) {
        addBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        cancelBtn.setVisibility(value ? View.GONE : View.VISIBLE);

        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    private final ServiceConnection paymentServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            status.setText("Payment device service connected");
            deviceService = ((DeviceService.LocalBinder) service).getService();
            deviceService.pairWithDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status.setText("Payment device service disconnected");
            deviceService = null;
        }
    };


    PaymentDeviceListener notificationListener = new PaymentDeviceListener() {
        @Override
        public void onDeviceStateChanged(@Connection.State int state) {
            switch (state) {
                case States.INITIALIZED:
                    status.setText("INITIALIZED");
                    break;
                case States.CONNECTED:
                    status.setText("CONNECTED");
                    deviceService.readDeviceInfo();
                    break;
                case States.DISCONNECTED:
                    status.setText("DISCONNECTED");
                    break;
                case States.CONNECTING:
                    status.setText("CONNECTING");
                    break;
                case States.DISCONNECTING:
                    status.setText("DISCONNECTING");
                    break;
            }

        }

        @Override
        public void onDeviceInfoReceived(Device device) {
            Log.d(TAG, "device info received: " + device);
            populateView(device);
            DeviceCreateActivity.this.device = device;
            showProgress(false);
        }

        @Override
        public void onNFCStateReceived(boolean isEnabled, byte errorCode) {
        }

        @Override
        public void onNotificationReceived(byte[] data) {
        }

        @Override
        public void onApplicationControlReceived(byte[] data) {
        }
    };

    private void populateView(Device device) {
        deviceName.setText(device.getDeviceName());
        manufacturerName.setText(device.getManufacturerName());
        modelNumber.setText(device.getModelNumber());
        serialNumber.setText(device.getSerialNumber());
        firmwareNumber.setText(device.getFirmwareRevision());
        hardwareRevision.setText(device.getHardwareRevision());
        softwareRevision.setText(device.getSerialNumber());
        systemId.setText(device.getSystemId());
    }


}
