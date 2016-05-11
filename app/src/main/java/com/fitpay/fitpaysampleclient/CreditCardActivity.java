package com.fitpay.fitpaysampleclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.card.CreditCard;

public class CreditCardActivity extends AppCompatActivity {

    private CreditCard currentCard;

    private TextView tvCardType;
    private TextView tvCardDefault;
    private TextView tvCardState;
    private TextView tvCardExpMonth;
    private TextView tvCardExpYear;
    private TextView tvCardName;
    private TextView tvCardId;
    private TextView tvCardNumber;
    private View progress;
    private Button acceptBtn;
    private Button declineBtn;
    private Button transactionsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        tvCardName = (TextView) findViewById(R.id.card_name);
        tvCardId = (TextView) findViewById(R.id.card_id);
        tvCardNumber = (TextView) findViewById(R.id.card_number);
        tvCardType = (TextView) findViewById(R.id.card_type);
        tvCardDefault = (TextView) findViewById(R.id.card_default);
        tvCardState = (TextView) findViewById(R.id.card_state);
        tvCardExpMonth = (TextView) findViewById(R.id.card_exp_month);
        tvCardExpYear = (TextView) findViewById(R.id.card_exp_year);

        progress = findViewById(R.id.progress);
        acceptBtn = (Button) findViewById(R.id.btn_accept);
        declineBtn = (Button) findViewById(R.id.btn_decline);
        transactionsBtn = (Button) findViewById(R.id.btn_transactions);
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTerms();
            }
        });
        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineTerms();
            }
        });
        transactionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreditCardActivity.this, TransactionsActivity.class);
                intent.putExtra("creditCard", currentCard);
                startActivity(intent);
            }
        });
        currentCard = getIntent().getParcelableExtra("creditCard");
        getSelfCreditCard();
    }

    private void getSelfCreditCard() {
        showProgress(true);
        currentCard.self(new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                setCardInfo(result);
                showProgress(false);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
                //todo show error
            }
        });
    }

    private void acceptTerms() {
        showProgress(true);
        currentCard.acceptTerms(new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                showProgress(false);
                //todo show success
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
            }
        });
    }

    private void declineTerms() {
        showProgress(true);
        currentCard.declineTerms(new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                showProgress(false);
                //todo show success
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
            }
        });
    }

    private void setCardInfo(CreditCard creditCard) {
        currentCard = creditCard;
        tvCardName.setText(currentCard.getName());
        tvCardId.setText(currentCard.getCreditCardId());
        tvCardNumber.setText(currentCard.getPan());

        tvCardType.setText(currentCard.getCardType());
        tvCardDefault.setText(currentCard.isDefault() ? "true" : "false");
        tvCardState.setText(currentCard.getState());
        tvCardExpMonth.setText(currentCard.getExpMonth().toString());
        tvCardExpYear.setText(currentCard.getExpYear().toString());
    }

    private void showProgress(boolean value) {
        acceptBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        declineBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        transactionsBtn.setVisibility(value ? View.GONE : View.VISIBLE);
        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }
}
