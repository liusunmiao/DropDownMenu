package com.lsm.dropdownmenu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SexAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private int selectPostion=0;

    public SexAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setSelectPostion(int selectPostion) {
        this.selectPostion = selectPostion;
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
        convertView= LayoutInflater.from(context).inflate(R.layout.sex_item,parent,false);
        TextView tvSex = convertView.findViewById(R.id.tv_sex);
        tvSex.setText(list.get(position));
        if(selectPostion==position){
            tvSex.setTextColor(0xff890c85);
        }else{
            tvSex.setTextColor(Color.BLACK);
        }
        return convertView;
    }
}
