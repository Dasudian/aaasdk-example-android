package com.dasudian.dsdaaaexample;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ListAdapter extends ArrayAdapter<ContactsInfo> {
    private int resourceId;

    public ListAdapter(Context context, int textViewResourceId, List<ContactsInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ContactsInfo info = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.phoneNumber = (TextView) view.findViewById(R.id.phone_number);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(info.getName());
        viewHolder.phoneNumber.setText(info.getPhoneNumber());
        return view;
    }

    class ViewHolder {
        TextView name;
        TextView phoneNumber;
    }

}
