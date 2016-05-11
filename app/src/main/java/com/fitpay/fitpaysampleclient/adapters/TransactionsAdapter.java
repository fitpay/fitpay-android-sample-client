package com.fitpay.fitpaysampleclient.adapters;
/*
 * Created by andrews on 23.03.16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fitpay.android.api.models.Transaction;
import com.fitpay.fitpaysampleclient.R;

import java.util.List;


public class TransactionsAdapter extends ArrayAdapter<Transaction> {

    private final int layoutId;
    private final LayoutInflater layoutInflater;
    private final List<Transaction> transactions;

    public TransactionsAdapter(Context context, int resource, List<Transaction> objects) {
        super(context, resource, objects);
        layoutId = resource;
        transactions = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Transaction transaction = getItem(position);
        ViewHolder holder;

        if (null == convertView) {
            convertView = layoutInflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.transactionName = (TextView) convertView.findViewById(R.id.item_name);
            holder.transactionNumber = (TextView) convertView.findViewById(R.id.item_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.transactionName.setText(transaction.getErchantType());
        holder.transactionNumber.setText(transaction.getTransactionId());

        return convertView;
    }

    private static class ViewHolder {
        TextView transactionName;
        TextView transactionNumber;
    }

}