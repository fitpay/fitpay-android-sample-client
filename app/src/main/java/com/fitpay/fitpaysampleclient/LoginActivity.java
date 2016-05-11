package com.fitpay.fitpaysampleclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fitpay.android.api.ApiManager;
import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.user.LoginIdentity;
import com.fitpay.android.utils.Constants;
import com.fitpay.android.utils.ValidationException;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.orhanobut.logger.Logger;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    static final String BASE_URL = "https://gi-de.pagare.me/";

    private EditText email;
    private EditText password;
    private Button loginBtn;
    private Button findDeviceBtn;
    private View progress;


    //TODO update config mechanism
    protected Map<String, String> getApiConfig(){
        Map<String, String> config = new HashMap<>();
        config.put(ApiManager.PROPERTY_API_BASE_URL, "https://gi-de.pagare.me/api/");
        config.put(ApiManager.PROPERTY_AUTH_BASE_URL, "https://gi-de.pagare.me");
        config.put(ApiManager.PROPERTY_CLIENT_ID, "pagare");
        config.put(ApiManager.PROPERTY_REDIRECT_URI, "https://demo.pagare.me");
        return config;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Security.addProvider(BouncyCastleProviderSingleton.getInstance());

        Logger.init(Constants.FIT_PAY_TAG);

        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);
        progress = findViewById(R.id.progress);
        loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        findDeviceBtn = (Button) findViewById(R.id.btn_find_ble);
        findDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectDevice = new Intent(LoginActivity.this, TestBLEActivity.class);
                startActivity(selectDevice);
            }
        });

        Button testMockeDeviceBtn = (Button) findViewById(R.id.btn_test_mock_device);
        testMockeDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectDevice = new Intent(LoginActivity.this, TestBLEActivity.class);
                selectDevice.putExtra("paymentDeviceType", "MockDevice");
                startActivity(selectDevice);
            }
        });
    }

    private void login() {
        String login = email.getText().toString();
        String pass = password.getText().toString();
        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(pass)) {
            LoginIdentity loginIdentity = null;
            try {
                loginIdentity = new LoginIdentity.Builder()
                        .setUsername(login)
                        .setPassword(pass)
                        .build();
            } catch (ValidationException ignored) {
            }
            showProgress(true);
            ApiManager.init(getApiConfig());
            ApiManager.getInstance().loginUser(loginIdentity, new ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    goToMainScreen();
                }

                @Override
                public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                    showProgress(false);
                    email.setError(errorMessage);
                }
            });

        }
    }

    private void showProgress(boolean value) {
        loginBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    private void goToMainScreen() {
        startActivity(new Intent(this, MainActivity.class));
        showProgress(false);
        finish();
    }

}
