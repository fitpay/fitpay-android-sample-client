package com.fitpay.fitpaysampleclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.card.Reason;

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
    private Button deactivateBtn;
    private Button reactivateBtn;
    private Button makeDefaultBtn;
    private Button updateBtn;
    private Button deleteBtn;
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
        reactivateBtn = (Button) findViewById(R.id.btn_reactivate);
        deactivateBtn = (Button) findViewById(R.id.btn_deactivate);
        makeDefaultBtn = (Button) findViewById(R.id.btn_make_default);
        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);
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
        deactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactivate();
            }
        });
        reactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reactivate();
            }
        });
        makeDefaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDefault();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreditCardActivity.this, "Not implemented", Toast.LENGTH_LONG).show();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
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
        // TODO - verify this is not needed and remove
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
                Toast.makeText(CreditCardActivity.this, "Could not get card.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private void acceptTerms() {
        showProgress(true);
        currentCard.acceptTerms(new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                setCardInfo(currentCard);
                showProgress(false);
                Toast.makeText(CreditCardActivity.this, "Card terms accepted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Toast.makeText(CreditCardActivity.this, "Accept terms was not successful.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
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
                setCardInfo(currentCard);
                showProgress(false);
                Toast.makeText(CreditCardActivity.this, "Card terms declined", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Toast.makeText(CreditCardActivity.this, "Decline terms was not successful.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private void delete() {
        showProgress(true);
        currentCard.deleteCard(new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // can not get card to refresh data - so do manually
                tvCardState.setText("DELETED");
                showProgress(false);
                enableActions(false);
                Toast.makeText(CreditCardActivity.this, "Card deleted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Toast.makeText(CreditCardActivity.this, "Delete was not successful.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private void deactivate() {
        showProgress(true);
        Reason reason = new Reason();
        reason.setReason("card lost");
        reason.setCausedBy("CARDHOLDER");
        currentCard.deactivate(reason, new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                setCardInfo(currentCard);
                showProgress(false);
                Toast.makeText(CreditCardActivity.this, "Card deactivated", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Toast.makeText(CreditCardActivity.this, "Deactivate was not successful.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private void reactivate() {
        showProgress(true);
        Reason reason = new Reason();
        reason.setReason("the card is back");
        reason.setCausedBy("CARDHOLDER");
        currentCard.reactivate(reason, new ApiCallback<CreditCard>() {
            @Override
            public void onSuccess(CreditCard result) {
                currentCard = result;
                setCardInfo(currentCard);
                showProgress(false);
                Toast.makeText(CreditCardActivity.this, "Card reactivated", Toast.LENGTH_LONG).show();
           }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Toast.makeText(CreditCardActivity.this, "Deactivate was not successful.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    private void makeDefault() {
        showProgress(true);
        currentCard.makeDefault(new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // need to refresh the card data
                getSelfCreditCard();
                Toast.makeText(CreditCardActivity.this, "Card designated as default card", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                Toast.makeText(CreditCardActivity.this, "Deactivate was not successful.  Reason: " + errorMessage, Toast.LENGTH_LONG).show();
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
        progress.setVisibility(value ? View.VISIBLE : View.GONE);
        if (!value) {
            enableAvailableActions();
        } else {
            enableActions(false);
        }
    }

    private void enableAvailableActions() {
        acceptBtn.setEnabled(currentCard.canAcceptTerms());
        declineBtn.setEnabled(currentCard.canDeclineTerms());
        deactivateBtn.setEnabled(currentCard.canDeactivate());
        reactivateBtn.setEnabled(currentCard.canReactivate());
        makeDefaultBtn.setEnabled(currentCard.canMakeDefault());
        updateBtn.setEnabled(currentCard.canUpdateCard());
        deleteBtn.setEnabled(currentCard.canDelete());
        transactionsBtn.setEnabled(currentCard.canGetTransactions());
    }

    private void enableActions(boolean enable) {
        acceptBtn.setEnabled(enable);
        declineBtn.setEnabled(enable);
        deactivateBtn.setEnabled(enable);
        reactivateBtn.setEnabled(enable);
        makeDefaultBtn.setEnabled(enable);
        updateBtn.setEnabled(enable);
        deleteBtn.setEnabled(enable);
        transactionsBtn.setEnabled(enable);
    }
}
