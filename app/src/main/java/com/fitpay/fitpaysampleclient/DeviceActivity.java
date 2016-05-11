package com.fitpay.fitpaysampleclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.device.Device;
import com.fitpay.fitpaysampleclient.adapters.CommitsAdapter;

public class DeviceActivity extends AppCompatActivity {

    private TextView tvDeviceName;
    private TextView tvDeviceId;
    private View progress;
    private Button userBtn;
    private Button commitsBtn;
    private Device currentDevice;
    private Collections.CommitsCollection commitCollection;
    private ListView listView;
    private CommitsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        tvDeviceName = (TextView) findViewById(R.id.device_name);
        tvDeviceId = (TextView) findViewById(R.id.device_id);
        progress = findViewById(R.id.progress);

        userBtn = (Button) findViewById(R.id.btn_user);
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
                getCommits();
            }
        });
        listView = (ListView) findViewById(R.id.list);
        currentDevice = getIntent().getParcelableExtra("device");
        getSelfDevice();
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
        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }
}
