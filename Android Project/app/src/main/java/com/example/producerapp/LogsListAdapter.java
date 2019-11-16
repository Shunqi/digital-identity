package com.example.producerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LogsListAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<LogItem> objects;

    LogsListAdapter(Context context, ArrayList<LogItem> items) {
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.log_list_item, parent, false);
        }

        LogItem p = getLogItem(position);

        ((TextView) view.findViewById(R.id.log_textView)).setText(p.text);

        return view;
    }

    LogItem getLogItem(int position) {
        return ((LogItem) getItem(position));
    }

    ArrayList<LogItem> getBox() {
        ArrayList<LogItem> box = new ArrayList();
        for (LogItem p : objects) {
            box.add(p);
        }
        return box;
    }


}
