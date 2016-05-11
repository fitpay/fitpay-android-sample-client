package com.fitpay.fitpaysampleclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.Transaction;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.fitpaysampleclient.adapters.TransactionsAdapter;

import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    private View progress;
    private ListView listView;
    private CreditCard currentCard;
    private List<Transaction> collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        progress = findViewById(R.id.progress_transactions);
        listView = (ListView) findViewById(R.id.list);
        currentCard = getIntent().getParcelableExtra("creditCard");
        getTransactions();
    }

    private void getTransactions() {
        showProgress(true);
        currentCard.getTransactions(10, 0, new ApiCallback<Collections.TransactionCollection>() {
            @Override
            public void onSuccess(Collections.TransactionCollection result) {
                collection = result.getResults();
                updateViews();
                showProgress(false);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
            }
        });
    }

    private void updateViews() {
        TransactionsAdapter mAdapter = new TransactionsAdapter(this, R.layout.device_item, collection);
        listView.setAdapter(mAdapter);
    }

    private void showProgress(boolean value) {
        listView.setVisibility(value ? View.GONE : View.VISIBLE);
        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }

}
