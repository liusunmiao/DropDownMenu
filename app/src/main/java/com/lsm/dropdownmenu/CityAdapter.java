package com.lsm.dropdownmenu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CityAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    //选中位置
    private int selectPostion=0;

    public CityAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
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
        convertView= LayoutInflater.from(context).inflate(R.layout.city_item,parent,false);
        TextView tvCity= convertView.findViewById(R.id.tv_city);
        tvCity.setText(list.get(position));
        if(selectPostion==position){
            tvCity.setTextColor(0xff890c85);
        }else{
            tvCity.setTextColor(Color.BLACK);
        }
        return convertView;
    }
}
