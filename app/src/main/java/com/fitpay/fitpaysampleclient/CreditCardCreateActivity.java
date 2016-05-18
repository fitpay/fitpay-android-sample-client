package com.fitpay.fitpaysampleclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.card.Address;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.user.User;

public class CreditCardCreateActivity extends AppCompatActivity {

    private User user;
    private CreditCard creditCard;

    private EditText tvCardName;
    private EditText tvCardNumber;
    private EditText tvCardCvv;

    private EditText tvCardExpMonth;
    private EditText tvCardExpYear;

    private EditText tvStreet1;
    private EditText tvCity;
    private EditText tvState;
    private EditText tvPostalCode;
    private EditText tvCountryCode;

    private View progress;

    private Button addBtn;
    private Button cancelBtn;
    private Button populateVisaBtn;
    private Button populateMastercardBtn;
    private Button populateAmexBtn;
    private Button populateDiscoverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_create);

        tvCardName = (EditText) findViewById(R.id.card_name);
        tvCardNumber = (EditText) findViewById(R.id.card_number);
        tvCardCvv = (EditText) findViewById(R.id.card_cvv);

        tvCardExpMonth = (EditText) findViewById(R.id.card_exp_month);
        tvCardExpYear = (EditText) findViewById(R.id.card_exp_year);

        tvStreet1 = (EditText) findViewById(R.id.street1);
        tvCity = (EditText) findViewById(R.id.city);
        tvState = (EditText) findViewById(R.id.state);
        tvPostalCode = (EditText) findViewById(R.id.postal_code);
        tvCountryCode = (EditText) findViewById(R.id.country_code);

        tvCountryCode.setText("US");

        progress = findViewById(R.id.progress);

        addBtn = (Button) findViewById(R.id.btn_add);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);

        populateVisaBtn = (Button) findViewById(R.id.btn_populate_visa);
        populateMastercardBtn = (Button) findViewById(R.id.btn_populate_mastercard);
        populateDiscoverBtn = (Button) findViewById(R.id.btn_populate_discover);
        populateAmexBtn = (Button) findViewById(R.id.btn_populate_amex);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCard();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        populateVisaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreditCardCreateActivity.this, "Not implemented", Toast.LENGTH_LONG).show();
            }
        });
        populateVisaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreditCardCreateActivity.this, "Not implemented", Toast.LENGTH_LONG).show();
            }
        });
        populateVisaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreditCardCreateActivity.this, "Not implemented", Toast.LENGTH_LONG).show();
            }
        });
        populateVisaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreditCardCreateActivity.this, "Not implemented", Toast.LENGTH_LONG).show();
            }
        });

        user = getIntent().getParcelableExtra("user");

        showProgress(false);

    }


    private void addCard() {
        showProgress(true);
        CreditCard newCard = getCreditCardDataFromForm();
        if (null == newCard) {
            showProgress(false);
            return;
        }
        user.createCreditCard(newCard, new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                creditCard = result;
                showProgress(false);
                Toast.makeText(CreditCardCreateActivity.this, "Card created", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Toast.makeText(CreditCardCreateActivity.this, "Create card was not successful.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private CreditCard getCreditCardDataFromForm() {

        int expMonth = 0;
        try {
            expMonth = Integer.parseInt(tvCardExpMonth.getText().toString());
        } catch (NumberFormatException nfe) {
            Toast.makeText(CreditCardCreateActivity.this, "Invalid expiration month value", Toast.LENGTH_LONG).show();
        }

        int expYear = 0;
        try {
            expYear = Integer.parseInt(tvCardExpYear.getText().toString());
        } catch (NumberFormatException nfe) {
            Toast.makeText(CreditCardCreateActivity.this, "Invalid expiration year value", Toast.LENGTH_LONG).show();
        }

        Address address = new Address.Builder()
                .setStreet1(tvStreet1.getText().toString())
                .setCity(tvCity.getText().toString())
                .setState(tvState.getText().toString())
                .setPostalCode(tvPostalCode.getText().toString())
                .setCountryCode(tvCountryCode.getText().toString())
                .build();
        CreditCard card = new CreditCard.Builder()
                .setName(tvCardName.getText().toString())
                .setPAN(tvCardNumber.getText().toString())
                .setExpDate(expYear, expMonth)
                .setAddress(address)
                .build();
        return card;
    }

    private void showProgress(boolean value) {
        addBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        cancelBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        populateVisaBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        populateMastercardBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        populateAmexBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        populateDiscoverBtn.setVisibility(value ? View.GONE : View.VISIBLE);

        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }



}
