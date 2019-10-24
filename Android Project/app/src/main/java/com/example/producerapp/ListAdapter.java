package com.example.producerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<PermissionItem> objects;

    ListAdapter(Context context, ArrayList<PermissionItem> items) {
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
            view = lInflater.inflate(R.layout.permission_list_item, parent, false);
        }

        PermissionItem p = getPermissionItem(position);

        ((TextView) view.findViewById(R.id.permission_title)).setText(p.title);

        CheckBox cbBuy = view.findViewById(R.id.permission_checkbox);
        cbBuy.setOnCheckedChangeListener(myCheckChangList);
        cbBuy.setTag(position);
        cbBuy.setChecked(p.box);
        return view;
    }

    PermissionItem getPermissionItem(int position) {
        return ((PermissionItem) getItem(position));
    }

    ArrayList<PermissionItem> getBox() {
        ArrayList<PermissionItem> box = new ArrayList();
        for (PermissionItem p : objects) {
            if (p.box)
                box.add(p);
        }
        return box;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getPermissionItem((Integer) buttonView.getTag()).box = isChecked;
        }
    };
}
