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

public class PermissionListAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<PermissionItem> objects;

    PermissionListAdapter(Context context, ArrayList<PermissionItem> items) {
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
            view = lInflater.inflate(R.layout.permission_list_group_item, parent, false);
        }

        PermissionItem p = getPermissionItem(position);

        ((TextView) view.findViewById(R.id.permission_category)).setText(p.category);

        CheckBox readbox = view.findViewById(R.id.permission_read);
        readbox.setOnCheckedChangeListener(readmyCheckChangList);
        readbox.setTag(position);
        readbox.setChecked(p.readbox);

        CheckBox writebox = view.findViewById(R.id.permission_write);
        writebox.setOnCheckedChangeListener(writemyCheckChangList);
        writebox.setTag(position);
        writebox.setChecked(p.writebox);

        CheckBox sharebox = view.findViewById(R.id.permission_share);
        sharebox.setOnCheckedChangeListener(sharemyCheckChangList);
        sharebox.setTag(position);
        sharebox.setChecked(p.sharebox);

        return view;
    }

    PermissionItem getPermissionItem(int position) {
        return ((PermissionItem) getItem(position));
    }

    ArrayList<PermissionItem> getBox() {
        ArrayList<PermissionItem> box = new ArrayList();
        for (PermissionItem p : objects) {
            box.add(p);
        }
        return box;
    }

    CompoundButton.OnCheckedChangeListener readmyCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getPermissionItem((Integer) buttonView.getTag()).readbox = isChecked;

        }
    };
    CompoundButton.OnCheckedChangeListener writemyCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getPermissionItem((Integer) buttonView.getTag()).writebox = isChecked;

        }
    };
    CompoundButton.OnCheckedChangeListener sharemyCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getPermissionItem((Integer) buttonView.getTag()).sharebox = isChecked;
        }
    };
}
