package com.lsm.dropdownmenu;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GridDropDownAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    private List<String> selectList;

    public GridDropDownAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        this.selectList = new ArrayList<>();
    }

    public void setSelectList(List<String> selectList) {
        this.selectList = selectList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.constellation_item, parent, false);
        TextView tvItem = convertView.findViewById(R.id.tv_item);
        String s = list.get(position);
        tvItem.setText(s);
        if(selectList.contains(s)){
            tvItem.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
        }else{
            tvItem.setTextColor(Color.BLACK);
        }
        return convertView;
    }
}
