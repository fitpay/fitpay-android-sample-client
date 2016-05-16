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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.paymentdevice.DeviceService;
import com.fitpay.android.paymentdevice.callbacks.PaymentDeviceListener;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.utils.DevicePreferenceData;
import com.fitpay.android.utils.NotificationManager;
import com.fitpay.fitpaysampleclient.adapters.CommitsAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceActivity extends AppCompatActivity {

    public static final String TAG = DeviceActivity.class.getSimpleName();

    @Bind(R.id.status)
    TextView status;
    @Bind(R.id.device_name)
    TextView tvDeviceName;
    @Bind(R.id.device_id)
    TextView tvDeviceId;
    @Bind(R.id.progress)
    View progress;
    @Bind(R.id.btn_user)
    Button userBtn;
    @Bind(R.id.btn_commits)
    Button commitsBtn;
    @Bind(R.id.btn_delete)
    Button deleteBtn;
    @Bind(R.id.btn_update)
    Button updateBtn;
    @Bind(R.id.btn_sync)
    Button syncBtn;

    @Bind(R.id.list)
    ListView listView;

    private Device currentDevice;
    private Collections.CommitsCollection commitCollection;
    private CommitsAdapter mAdapter;
    private DeviceService deviceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        Log.d(TAG, "create");

        commitsBtn = (Button) findViewById(R.id.btn_commits);

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUser();
            }
        });
        commitsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "get commits");
                getCommits();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeviceActivity.this, "Not implemented", Toast.LENGTH_SHORT).show();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sync();
            }
        });

        currentDevice = getIntent().getParcelableExtra("device");
        getSelfDevice();
    }

    private void sync() {
        if (null == currentDevice) {
            Toast.makeText(DeviceActivity.this, "Can not sync - no current device", Toast.LENGTH_LONG).show();
            return;
        }
        if (!currentDevice.canGetCommits()) {
            Toast.makeText(DeviceActivity.this, "Can not sync - can not get commits (missing link)", Toast.LENGTH_LONG).show();
            return;
        }
        bindDeviceService();
    }


    private void getSelfDevice() {
        showProgress(true);
        currentDevice.self(new ApiCallback<Device>() {
            @Override
            public void onSuccess(Device result) {
                setDeviceInfo(result);
                showProgress(false);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
                //todo show error
            }
        });
    }

    private void gotoUser() {
        //todo send User object
        startActivity(new Intent(this, MainActivity.class));
        showProgress(false);
        finish();
    }

    private void getCommits() {
        showProgress(true);
        currentDevice.getCommits(10, 0, new ApiCallback<Collections.CommitsCollection>() {
            @Override
            public void onSuccess(Collections.CommitsCollection result) {
                commitCollection = result;
                updateCommits();
                showProgress(false);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
            }
        });
    }

    private void delete() {
        showProgress(true);
        currentDevice.deleteDevice(new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                showProgress(false);
                Toast.makeText(DeviceActivity.this, "Device successfully deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
                Toast.makeText(DeviceActivity.this, "Could not delete device.  errorCode: " + errorCode + ": " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindDeviceService() {
        DevicePreferenceData data = DevicePreferenceData.loadFromPreferences(this, currentDevice.getDeviceIdentifier());
        if (null == data.getPaymentDeviceServiceType()) {
            Toast.makeText(DeviceActivity.this, "Can not sync - device is not paired", Toast.LENGTH_LONG).show();
            return;
        }
        NotificationManager.getInstance().addListener(notificationListener);
        Intent deviceServiceIntent = new Intent(this, DeviceService.class);
        deviceServiceIntent.putExtra(DeviceService.EXTRA_PAYMENT_SERVICE_TYPE, data.getPaymentDeviceServiceType());
        bindService(deviceServiceIntent, paymentServiceConnection, BIND_AUTO_CREATE);
    }

    private final ServiceConnection paymentServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //status.setText("Payment device service connected");
            deviceService = ((DeviceService.LocalBinder) service).getService();
            deviceService.pairWithDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //status.setText("Payment device service disconnected");
            deviceService = null;
        }
    };


    private void updateCommits() {
        mAdapter = new CommitsAdapter(this, R.layout.device_item, commitCollection.getResults());
        listView.setAdapter(mAdapter);
    }

    private void setDeviceInfo(Device device) {
        currentDevice = device;
        tvDeviceName.setText(currentDevice.getDeviceName());
        tvDeviceId.setText(currentDevice.getDeviceIdentifier());
    }

    private void showProgress(boolean value) {
        commitsBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        userBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        deleteBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        updateBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        syncBtn.setVisibility(value ? View.GONE : View.VISIBLE);

        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    PaymentDeviceListener notificationListener = new PaymentDeviceListener() {
        @Override
        public void onDeviceStateChanged(@Connection.State int state) {
            switch (state) {
                case States.INITIALIZED:
                    status.setText("INITIALIZED");
                    break;
                case States.CONNECTED:
                    status.setText("CONNECTED");
                    deviceService.syncData(currentDevice);
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

}
