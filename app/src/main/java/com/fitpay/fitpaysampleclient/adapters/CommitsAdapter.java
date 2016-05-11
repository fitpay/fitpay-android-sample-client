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

import com.fitpay.android.api.models.device.Commit;
import com.fitpay.fitpaysampleclient.R;

import java.util.List;


public class CommitsAdapter extends ArrayAdapter<Commit> {

    private final int layoutId;
    private final LayoutInflater layoutInflater;
    private final List<Commit> commits;

    public CommitsAdapter(Context context, int resource, List<Commit> objects) {
        super(context, resource, objects);
        layoutId = resource;
        commits = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Commit commit = getItem(position);
        ViewHolder holder;

        if (null == convertView) {
            convertView = layoutInflater.inflate(layoutId, parent, false);
            holder = new ViewHolder();
            holder.commitName = (TextView) convertView.findViewById(R.id.item_name);
            holder.commitNumber = (TextView) convertView.findViewById(R.id.item_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.commitName.setText(commit.getCommitType());
        holder.commitNumber.setText(commit.getCommitId());

        return convertView;
    }

    private static class ViewHolder {
        TextView commitName;
        TextView commitNumber;
    }

}