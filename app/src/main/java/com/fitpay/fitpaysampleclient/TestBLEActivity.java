package com.fitpay.fitpaysampleclient;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fitpay.android.api.models.apdu.ApduExecutionResult;
import com.fitpay.android.api.models.apdu.ApduPackage;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.android.api.models.user.User;
import com.fitpay.android.paymentdevice.DeviceService;
import com.fitpay.android.paymentdevice.callbacks.ApduExecutionListener;
import com.fitpay.android.paymentdevice.callbacks.PaymentDeviceListener;
import com.fitpay.android.paymentdevice.constants.States;
import com.fitpay.android.paymentdevice.enums.Connection;
import com.fitpay.android.paymentdevice.impl.ble.BluetoothPaymentDeviceService;
import com.fitpay.android.paymentdevice.impl.mock.MockPaymentDeviceService;
import com.fitpay.android.paymentdevice.interfaces.IPaymentDeviceService;
import com.fitpay.android.utils.Hex;
import com.fitpay.android.utils.NotificationManager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Vlad on 23.03.2016.
 */
public class TestBLEActivity extends AppCompatActivity {

    public static final String TAG = TestBLEActivity.class.getSimpleName();

    private static final int SELECT_DEVICE = 1010;
    @Bind(R.id.status)
    TextView status;
    @Bind(R.id.connect)
    Button connect;
    @Bind(R.id.disconnect)
    Button disconnect;
    @Bind(R.id.getDevice)
    Button getDevice;
    @Bind(R.id.resetDevice)
    Button resetDevice;
    @Bind(R.id.nfcValue)
    EditText nfcValue;
    @Bind(R.id.setNFC)
    Button setNFC;
    @Bind(R.id.apduValue)
    EditText apduValue;
    @Bind(R.id.sendApdu)
    Button sendApdu;
    @Bind(R.id.syncData)
    Button syncData;

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

    @Bind(R.id.apduArea)
    TextView apduArea;
    @Bind(R.id.loadApduPackage)
    Button loadApduPackage;



    private DeviceService deviceService;
    private BluetoothDevice bluetoothDevice;

    private IPaymentDeviceService paymentDevice;

    private PaymentDeviceListener mNotificationListener;
    private ApduExecutionListener mApduExecutionListener;

    private User currentUser;
    private Device currentDevice;

    private String paymentDeviceType = "FitpayBLE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_ble_activity);
        ButterKnife.bind(this);

        apduArea.setMovementMethod(new ScrollingMovementMethod());
        apduArea.setText(getTestApduPackage());

        Intent gattIntent = new Intent(this, DeviceService.class);
        bindService(gattIntent, paymentServiceConnection, BIND_AUTO_CREATE);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("paymentDeviceType")) {
            paymentDeviceType = intent.getExtras().getString("paymentDeviceType");
        }

        switch (paymentDeviceType) {
            case "MockDevice": {
                break;
            }
            default: {
                Intent selectDevice = new Intent(this, SelectDeviceActivity.class);
                startActivityForResult(selectDevice, SELECT_DEVICE);
            }
        }

        mNotificationListener = new PaymentDeviceListener() {
            @Override
            public void onDeviceStateChanged(@Connection.State int state) {
                switch (state) {
                    case States.INITIALIZED:
                        status.setText("INITIALIZED");
                        break;
                    case States.CONNECTED:
                        status.setText("CONNECTED");
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
                manufacturerName.setText(device.getManufacturerName());
                modelNumber.setText(device.getModelNumber());
                serialNumber.setText(device.getSerialNumber());
                firmwareNumber.setText(device.getFirmwareRevision());
                hardwareRevision.setText(device.getHardwareRevision());
                softwareRevision.setText(device.getSerialNumber());
                systemId.setText(device.getSystemId());
                Toast.makeText(TestBLEActivity.this, "Device: " + device.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNFCStateReceived(boolean isEnabled, byte errorCode) {
                Toast.makeText(TestBLEActivity.this, "NFC: " + isEnabled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotificationReceived(byte[] data) {
                String message = Hex.bytesToHexString(data);
                Toast.makeText(TestBLEActivity.this, "Transaction: " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApplicationControlReceived(byte[] data) {
                String message = Hex.bytesToHexString(data);
                Toast.makeText(TestBLEActivity.this, "Control: " + message, Toast.LENGTH_SHORT).show();
            }
        };

        mApduExecutionListener = new ApduExecutionListener() {
            @Override
            public void onApduPackageResultReceived(ApduExecutionResult result) {
                Log.d(TAG, "handling apdu result recieved.  state: " + result.getState()
                        + ", packageId: " + result.getPackageId());
                String response = new Gson().toJson(result);
                Log.d(TAG, "Apdu package success response: " + response);
                apduArea.setText(response);

                Toast.makeText(TestBLEActivity.this, "Apdu package processing completed.  state: "
                        + result.getState() + ", packageId: " + result.getPackageId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onApduPackageErrorReceived(ApduExecutionResult result) {
                Log.d(TAG, "handling apdu error received.   state: " + result.getState()
                        + ", packageId: "+ result.getPackageId());
                String response = new Gson().toJson(result);
                Log.d(TAG, "Apdu package error response: " + response);
                apduArea.setText(response);
                Toast.makeText(TestBLEActivity.this, "Apdu package processing ERROR:   state: "
                        + result.getState() + ", packageId: "+ result.getPackageId(), Toast.LENGTH_LONG).show();
            }
        };

        NotificationManager.getInstance().addListener(mNotificationListener);
        NotificationManager.getInstance().addListener(mApduExecutionListener);

        syncData.setEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NotificationManager.getInstance().removeListener(mNotificationListener);
        NotificationManager.getInstance().removeListener(mApduExecutionListener);

        if (deviceService != null) {
            deviceService.disconnect();
        }

        unbindService(paymentServiceConnection);
        deviceService = null;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK && data != null) {
            bluetoothDevice = (BluetoothDevice) data.getExtras().get("device");
            if (bluetoothDevice != null) {
                status.setText("GOT DEVICE");
            } else {
                status.setText("NO DEVICE");
            }
        }
    }

    private void pairWithDevice() {
        if (deviceService != null) {
            Log.d(TAG, "current payment device: " + paymentDevice);

            if (paymentDevice == null) {
                paymentDevice = deviceService.getPairedDevice();
                Log.d(TAG, "device service is using payment device: " + paymentDevice);
            }

            if (null == paymentDevice) {
                switch (paymentDeviceType) {
                    case "MockDevice": {
                        Log.d(TAG, "Creating new mock payment device");
                        paymentDevice = new MockPaymentDeviceService();
                        break;
                    }
                    default: {  // bluetooth device
                        if (null != bluetoothDevice) {
                            Log.d(TAG, "creating new bluetooth payment device");
                            paymentDevice = new BluetoothPaymentDeviceService(getApplicationContext(), bluetoothDevice.getAddress());
                        } else {
                            Log.d(TAG, "did not create bluetooth payment device since no address defined");
                        }
                    }
                }
            }

            if (paymentDevice != null) {
                deviceService.pairWithDevice(paymentDevice);
            } else {
                Log.e(TAG, "Payment device is not defined.  This is a client application defect.");
            }
        } else {
            Log.e(TAG, "Device service is not defined.  This is a client application defect.");
        }
    }

    private final ServiceConnection paymentServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            status.setText("Payment device service connected");
            deviceService = ((DeviceService.LocalBinder) service).getService();
            pairWithDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status.setText("Payment device service disconnected");
            deviceService = null;
        }
    };

    @OnClick({R.id.connect, R.id.disconnect, R.id.getDevice, R.id.resetDevice, R.id.setNFC, R.id.sendApdu, R.id.syncData, R.id.loadApduPackage})
    public void onClick(View view) {

        if(paymentDevice == null && view.getId() != R.id.connect){
            Toast.makeText(TestBLEActivity.this, "You need to connect at first", Toast.LENGTH_LONG).show();
            return;
        }

        switch (view.getId()) {
            case R.id.connect:
                pairWithDevice();
                break;
            case R.id.disconnect:
                paymentDevice.disconnect();
                break;
            case R.id.getDevice:
                paymentDevice.readDeviceInfo();
                break;
            case R.id.resetDevice:
                paymentDevice.setSecureElementState(States.RESET);
                break;
            case R.id.setNFC:
                paymentDevice.setNFCState(nfcValue.getText().toString().equals("1") ? States.ENABLE : States.DISABLE);
                break;
            case R.id.loadApduPackage:
                apduArea.setText(getTestApduPackage());
                break;
            case R.id.sendApdu:

                String apduJson = apduArea.getText().toString();

                ApduPackage apdu = null;
                try {
                    apdu = new Gson().fromJson(apduJson, ApduPackage.class);
                } catch (JsonSyntaxException jse) {
                    Toast.makeText(TestBLEActivity.this, "Invalid APDU Package.  Please correct the input json representation", Toast.LENGTH_LONG).show();
                    return;
                }

                paymentDevice.executeApduPackage(apdu);
                break;

            case R.id.syncData:
                //TODO: put the device here
                deviceService.syncData(currentDevice);
        }
    }

    private String getTestApduPackage() {

        String apduJson = "{  \n" +
                "   \"seIdType\":\"iccid\",\n" +
                "   \"targetDeviceType\":\"fitpay.gandd.model.Device\",\n" +
                "   \"targetDeviceId\":\"72425c1e-3a17-4e1a-b0a4-a41ffcd00a5a\",\n" +
                "   \"packageId\":\"baff08fb-0b73-5019-8877-7c490a43dc64\",\n" +
                "   \"seId\":\"333274689f09352405792e9493356ac880c44444442\",\n" +
                "   \"targetAid\":\"8050200008CF0AFB2A88611AD51C\",\n" +
                "   \"commandApdus\":[  \n" +
                "      {  \n" +
                "         \"commandId\":\"5f2acf6f-536d-4444-9cf4-7c83fdf394bf\",\n" +
                "         \"groupId\":0,\n" +
                "         \"sequence\":0,\n" +
                "         \"command\":\"00E01234567890ABCDEF\",\n" +
                "         \"type\":\"CREATE FILE\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"00df5f39-7627-447d-9380-46d8574e0643\",\n" +
                "         \"groupId\":1,\n" +
                "         \"sequence\":1,\n" +
                "         \"command\":\"8050200008CF0AFB2A88611AD51C\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"9c719928-8bb0-459c-b7c0-2bc48ec53f3c\",\n" +
                "         \"groupId\":1,\n" +
                "         \"sequence\":2,\n" +
                "         \"command\":\"84820300106BBC29E6A224522E83A9B26FD456111500\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"b148bea5-6d98-4c83-8a20-575b4edd7a42\",\n" +
                "         \"groupId\":1,\n" +
                "         \"sequence\":3,\n" +
                "         \"command\":\"9800E01234567890ABCDEF84820300106BBC29E6A224522E83A9B26FD456111500\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"905fc5ab-4b15-4704-889b-2c5ffcfb2d68\",\n" +
                "         \"groupId\":2,\n" +
                "         \"sequence\":4,\n" +
                "         \"command\":\"84F2200210F25397DCFB728E25FBEE52E748A116A800\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      },\n" +
                "      {  \n" +
                "         \"commandId\":\"8e87ff12-dfc2-472a-bbf1-5f2e891e864c\",\n" +
                "         \"groupId\":3,\n" +
                "         \"sequence\":5,\n" +
                "         \"command\":\"84F2200210F25397DCFB728E25FBEE52E748A116A800\",\n" +
                "         \"type\":\"UNKNOWN\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"validUntil\":\"2020-12-11T21:22:58.691Z\",\n" +
                "   \"apduPackageUrl\":\"http://localhost:9103/transportservice/v1/apdupackages/baff08fb-0b73-5019-8877-7c490a43dc64\"\n" +
                "}";
        return apduJson;

    }
}
