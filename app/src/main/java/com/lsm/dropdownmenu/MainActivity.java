package com.lsm.dropdownmenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<String> tabTextList = Arrays.asList("城市", "年龄", "性别", "星座");
    private DropDownMenu dropDownMenu;
    private List<View> popupsView = new ArrayList<>();
    private String[] citys = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private String[] ages = {"不限", "18岁以下", "18-22岁", "23-26岁", "27-35岁", "35岁以上"};
    private String[] sexs = {"不限", "男", "女"};
    private String[] constellations = {"不限", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};
    private CityAdapter cityAdapter;
    private SexAdapter sexAdapter, ageAdapter;
    private GridDropDownAdapter dropDownAdapter;
    private TextView tvContent;
    private List<String> constellationSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dropDownMenu = findViewById(R.id.drop_down_view);
        constellationSelect = new ArrayList<>();
        initViews();
        View contentView = getLayoutInflater().inflate(R.layout.content_layout, null);
        dropDownMenu.setDropDownMenu(tabTextList, popupsView, contentView);
        tvContent = contentView.findViewById(R.id.tv_content);
        tvContent.setText("内容布局");
    }

    private void initViews() {
        ListView lvCity = new ListView(this);
        cityAdapter = new CityAdapter(this, Arrays.asList(citys));
        lvCity.setAdapter(cityAdapter);
        lvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityAdapter.setSelectPostion(position);
                String select = position == 0 ? "城市" : citys[position];
                tvContent.setText(select);
                dropDownMenu.setTabText(select);
                dropDownMenu.closeMenu();
            }
        });

        ListView ageListView = new ListView(this);
        ageAdapter = new SexAdapter(Arrays.asList(ages), this);
        ageListView.setAdapter(ageAdapter);
        ageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ageAdapter.setSelectPostion(position);
                String select = position == 0 ? "年龄" : ages[position];
                tvContent.setText(select);
                dropDownMenu.setTabText(select);
                dropDownMenu.closeMenu();
            }
        });

        ListView sexListView = new ListView(this);
        sexAdapter = new SexAdapter(Arrays.asList(sexs), this);
        sexListView.setAdapter(sexAdapter);
        sexListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sexAdapter.setSelectPostion(position);
                String select = position == 0 ? "性别" : sexs[position];
                tvContent.setText(select);
                dropDownMenu.setTabText(select);
                dropDownMenu.closeMenu();
            }
        });

        View constellationView = getLayoutInflater().inflate(R.layout.layout_constellation, null);
        GridView gridView = constellationView.findViewById(R.id.constellation);
        dropDownAdapter = new GridDropDownAdapter(this, Arrays.asList(constellations));
        gridView.setAdapter(dropDownAdapter);
        TextView tvOk = constellationView.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (constellationSelect.size() == 0) {
                    dropDownMenu.closeMenu();
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < constellationSelect.size(); i++) {
                    String s = constellationSelect.get(i);
                    if (!s.equals("不限")) {
                        if (i == constellationSelect.size() - 1) {
                            sb.append(s);
                        } else {
                            sb.append(s).append(",");
                        }
                    }
                }
                tvContent.setText(sb.toString());
                dropDownMenu.closeMenu();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String result = constellations[position];
                if (result.equals("不限")) {
                    constellationSelect.clear();
                } else {
                    if (constellationSelect.contains(result)) {
                        constellationSelect.remove(result);
                    } else {
                        constellationSelect.add(result);
                    }
                }
                dropDownAdapter.setSelectList(constellationSelect);
            }
        });
        popupsView.add(lvCity);
        popupsView.add(ageListView);
        popupsView.add(sexListView);
        popupsView.add(constellationView);
    }

    @Override
    public void onBackPressed() {
        if (dropDownMenu != null && dropDownMenu.isShowing()) {
            //显示就关闭
            dropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}
