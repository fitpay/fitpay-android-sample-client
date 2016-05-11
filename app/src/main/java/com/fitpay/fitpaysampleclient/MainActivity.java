package com.fitpay.fitpaysampleclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fitpay.android.api.ApiManager;
import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.user.User;


public class MainActivity extends AppCompatActivity {

    private User user;
    private TextView userName;
    private TextView userName1;
    private TextView userName2;

    private Button devices;
    private Button creditCards;
    private View progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (TextView) findViewById(R.id.tv_username_main);
        userName1 = (TextView) findViewById(R.id.tv_user_email);
        userName2 = (TextView) findViewById(R.id.tv_userdata);
        devices = (Button) findViewById(R.id.btn_devices);
        creditCards = (Button) findViewById(R.id.btn_credit_cards);
        progress = findViewById(R.id.progress);
        devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DevicesActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        creditCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreditCardsActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        getUser();
    }

    private void getUser() {
        showProgress(true);
        ApiManager.getInstance().getUser(new ApiCallback<User>() {
            @Override
            public void onSuccess(User result) {
                setUserInfo(result);
                showProgress(false);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
                //todo show error
            }
        });
    }

    private void setUserInfo(User userInfo) {
        user = userInfo;
        userName.setText(user.getUsername());
        userName1.setText(user.getEmail());
        userName2.setText(user.getId());
    }

    private void showProgress(boolean value) {
        devices.setVisibility(value ? View.GONE : View.VISIBLE);
        creditCards.setVisibility(value ? View.GONE : View.VISIBLE);
        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }

}
