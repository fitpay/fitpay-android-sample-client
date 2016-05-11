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

import com.fitpay.android.api.models.card.CreditCard;
import com.fitpay.fitpaysampleclient.R;

import java.util.List;


public class CreditCardsAdapter extends ArrayAdapter<CreditCard> {

    private final int layoutId;
    private final LayoutInflater layoutInflater;
    private final List<CreditCard> creditCards;

    public CreditCardsAdapter(Context context, int resource, List<CreditCard> objects) {
        super(context, resource, objects);
        layoutId = resource;
        creditCards = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CreditCard creditCard = getItem(position);
        ViewHolder holder;

        if (null == convertView) {
            convertView = layoutInflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.creditCardName = (TextView) convertView.findViewById(R.id.item_name);
            holder.creditCardNumber = (TextView) convertView.findViewById(R.id.item_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.creditCardName.setText(creditCard.getCardType());
        holder.creditCardNumber.setText(creditCard.getPan());

        return convertView;
    }

    private static class ViewHolder {
        TextView creditCardName;
        TextView creditCardNumber;
    }

}