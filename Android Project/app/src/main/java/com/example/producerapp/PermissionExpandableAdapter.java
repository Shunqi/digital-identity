package com.example.producerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

public class PermissionExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<PermissionItem> expandableListTitle;
    private HashMap<PermissionItem, List<ThirdPartyItem>> expandableListDetail;

    public PermissionExpandableAdapter(Context context, List<PermissionItem> expandableListTitle,
                                       HashMap<PermissionItem, List<ThirdPartyItem>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(final int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.third_party_list_item, null);
        }

        ThirdPartyItem p = getThirdPartyItem(listPosition, expandedListPosition);
        View view = convertView;
        ((TextView) view.findViewById(R.id.third_party_textView)).setText(p.thirdPartyName);

        CheckBox allowedbox = view.findViewById(R.id.third_party_checkBox);
        allowedbox.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getThirdPartyItem(listPosition, expandedListPosition).allowedbox = isChecked;
            }
        });
        allowedbox.setTag(listPosition);
        allowedbox.setChecked(p.allowedbox);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.permission_list_group_item, null);
        }

        PermissionItem p = getPermissionItem(listPosition);

        View view = convertView;
        ((TextView) view.findViewById(R.id.permission_category)).setText(p.category);

        CheckBox readbox = view.findViewById(R.id.permission_read);
        readbox.setOnCheckedChangeListener(readmyCheckChangeList);
        readbox.setTag(listPosition);
        readbox.setChecked(p.readbox);

        CheckBox writebox = view.findViewById(R.id.permission_write);
        writebox.setOnCheckedChangeListener(writemyCheckChangeList);
        writebox.setTag(listPosition);
        writebox.setChecked(p.writebox);

        CheckBox sharebox = view.findViewById(R.id.permission_share);
        sharebox.setOnCheckedChangeListener(sharemyCheckChangeList);
        sharebox.setTag(listPosition);
        sharebox.setChecked(p.sharebox);

        ExpandableListView expandableListView = (ExpandableListView)parent;
        if (!isExpanded) {
            expandableListView.expandGroup(listPosition);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    PermissionItem getPermissionItem(int position) {
        return ((PermissionItem) getGroup(position));
    }

    ThirdPartyItem getThirdPartyItem(int listPosition, int expandedListPosition){return (ThirdPartyItem) getChild(listPosition, expandedListPosition);}

    HashMap<PermissionItem, List<ThirdPartyItem>> getBox() {
        return this.expandableListDetail;
    }

    CompoundButton.OnCheckedChangeListener readmyCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getPermissionItem((Integer) buttonView.getTag()).readbox = isChecked;

        }
    };
    CompoundButton.OnCheckedChangeListener writemyCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getPermissionItem((Integer) buttonView.getTag()).writebox = isChecked;

        }
    };
    CompoundButton.OnCheckedChangeListener sharemyCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getPermissionItem((Integer) buttonView.getTag()).sharebox = isChecked;
        }
    };

}
