package com.fitpay.fitpaysampleclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.fitpay.android.api.callbacks.ApiCallback;
import com.fitpay.android.api.enums.ResultCode;
import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.android.api.models.collection.Collections;
import com.fitpay.android.api.models.user.User;
import com.fitpay.fitpaysampleclient.adapters.CreditCardsAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreditCardsActivity extends AppCompatActivity {

    private User user;
    private List<CreditCard> collection;
    private View progress;
    private ListView listView;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_cards);

        addBtn = (Button) findViewById(R.id.btn_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreditCardsActivity.this, CreditCardCreateActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        progress = findViewById(R.id.progress_devices);

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToCreditCard(position);
            }
        });
        user = (User) SessionStorage.getInstance().getData("user");
        getCreditCards();
    }

    private void getCreditCards() {
        if (null == user) {
            return;
        }
        showProgress(true);
        user.getCreditCards(10, 0, new ApiCallback<Collections.CreditCardCollection>() {
            @Override
            public void onSuccess(Collections.CreditCardCollection result) {
                collection = result.getResults();
                updateViews();
                showProgress(false);
            }

            @Override
            public void onFailure(@ResultCode.Code int errorCode, String errorMessage) {
                showProgress(false);
                //todo show error
            }
        });

    }


    private void updateViews() {
        if (collection == null) {
            List<CreditCard> creditCards = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                CreditCard testCard = new CreditCard.Builder().setName("CreditCard " + i).build();
                creditCards.add(testCard);
            }

            collection = creditCards;
        }
        CreditCardsAdapter mAdapter = new CreditCardsAdapter(this, R.layout.device_item, collection);
        listView.setAdapter(mAdapter);
    }

    private void goToCreditCard(int position) {
        CreditCard creditCard = collection.get(position);
        Intent intent = new Intent(CreditCardsActivity.this, CreditCardActivity.class);
        intent.putExtra("creditCard", creditCard);
        startActivity(intent);
    }

    private void showProgress(boolean value) {
        listView.setVisibility(value ? View.GONE : View.VISIBLE);
        progress.setVisibility(value ? View.VISIBLE : View.GONE);
    }

}
