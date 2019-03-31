package com.lsm.dropdownmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

public class DropDownMenu extends LinearLayout {
    //水平分割线颜色
    private int mUnderlineColor = 0xffcccccc;
    //水平分割线的高度
    private float mUnderlineHeight = 1f;
    //分割线颜色
    private int mDividerColor = 0xffcccccc;
    //文字被选的颜色
    private int mTextSelectColor = 0xff890c85;
    //文字正常显示的颜色
    private int mTextUnSelectColor = 0xff111111;
    //菜单背景颜色
    private int mMenuBackgroundColor = 0xffffffff;
    //背景遮罩颜色
    private int mMaskColor = 0x88888888;
    //文字大小
    private int mMenuTextSize = 14;
    //被选的箭头
    private int mMenuSelectIcon = R.mipmap.up_arrow;
    //正常显示的箭头
    private int mMenuUnSelectIcon = R.mipmap.down_arrow;
    //是否显示顶部tab栏中间的分割线
    private boolean mDividerShow = false;
    //订单菜单
    private LinearLayout tabMenuView;
    //容器布局 包含内容区域 遮罩区域 菜单弹出区域
    private FrameLayout containerView;
    //遮罩区域
    private View maskView;
    //菜单弹出区域
    private FrameLayout popupMenuViews;
    //选中tab项的位置
    private int currentTabPosition = -1;
    //如果列表使用的是recyclerView 这个是recyclerView的高度
    private int recyclerViewHeight = 0;

    public DropDownMenu(Context context) {
        this(context, null);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        initAttrs(context, attrs);
        initViews(context);
    }

    /**
     * 初始化自定义属性
     *
     * @param context
     * @param attrs
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        mUnderlineColor = array.getColor(R.styleable.DropDownMenu_underlineColor, mUnderlineColor);
        mDividerColor = array.getColor(R.styleable.DropDownMenu_dividerColor, mDividerColor);
        mTextSelectColor = array.getColor(R.styleable.DropDownMenu_textSelectColor, mTextSelectColor);
        mTextUnSelectColor = array.getColor(R.styleable.DropDownMenu_textUnSelectColor, mTextUnSelectColor);
        mMenuBackgroundColor = array.getColor(R.styleable.DropDownMenu_menuBackgroundColor, mMenuBackgroundColor);
        mMaskColor = array.getColor(R.styleable.DropDownMenu_maskColor, mMaskColor);
        mMenuTextSize = array.getDimensionPixelSize(R.styleable.DropDownMenu_menuTextSize, mMenuTextSize);
        mMenuSelectIcon = array.getResourceId(R.styleable.DropDownMenu_menuSelectIcon, mMenuSelectIcon);
        mMenuUnSelectIcon = array.getResourceId(R.styleable.DropDownMenu_menuUnSelectIcon, mMenuUnSelectIcon);
        mUnderlineHeight = array.getDimension(R.styleable.DropDownMenu_underlineHeight, dip2px(mUnderlineHeight));
        mDividerShow = array.getBoolean(R.styleable.DropDownMenu_dividerShow, mDividerShow);
        array.recycle();
    }

    private void initViews(Context context) {
        //创建顶部tab
        tabMenuView = new LinearLayout(context);
        FrameLayout.LayoutParams tabLp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tabMenuView.setOrientation(HORIZONTAL);
        tabMenuView.setLayoutParams(tabLp);
        addView(tabMenuView, 0);
        //创建水平下滑线
        View underlineView = new View(context);
        FrameLayout.LayoutParams lineLp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mUnderlineHeight);
        underlineView.setBackgroundColor(mUnderlineColor);
        underlineView.setLayoutParams(lineLp);
        addView(underlineView, 1);

        //初始化containerView
        containerView = new FrameLayout(context);
        FrameLayout.LayoutParams contentLp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        containerView.setLayoutParams(contentLp);
        addView(containerView, 2);
    }

    /**
     * 初始化DropMenuView 用来显示内容
     */
    public void setDropDownMenu(List<String> tabTextList, List<View> popuViews, View contentView) {
        if (tabTextList.size() != popuViews.size()) {
            throw new IllegalArgumentException("tabTextList size should be equal popuViews size");
        }
        for (int i = 0; i < tabTextList.size(); i++) {
            addTab(tabTextList, i);
        }
        containerView.addView(contentView, 0);
        //添加遮罩层
        maskView = new View(getContext());
        maskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        maskView.setBackgroundColor(mMaskColor);
        maskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        maskView.setVisibility(View.GONE);
        containerView.addView(maskView, 1);
        //添加内容view
        popupMenuViews = new FrameLayout(getContext());
        popupMenuViews.setVisibility(View.GONE);
        for (int i = 0; i < popuViews.size(); i++) {
            final View view = popuViews.get(i);
            view.setBackgroundColor(Color.WHITE);
            if (recyclerViewHeight != 0) {
                view.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, recyclerViewHeight));
            } else {
                int totalHeight = getTotalHeight(view);
                if (totalHeight != 0) {
                    view.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, totalHeight));
                } else {
                    view.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                }
            }
            popupMenuViews.addView(popuViews.get(i), i);
        }
        containerView.addView(popupMenuViews, 2);
    }

    /**
     * 如果是listview或者gridview 获取它们所有子条目的总高度
     *
     * @param view
     * @return
     */
    private int getTotalHeight(View view) {
        int result = 0;
        if (view instanceof AbsListView) {
            //如果是listview或者gridview 获取它们所有子条目的总高度
            AbsListView listView = (AbsListView) view;
            ListAdapter adapter = listView.getAdapter();
            if (adapter == null) {
                return result;
            }
            int count = adapter.getCount();
            for (int j = 0; j < count; j++) {
                View listItem = adapter.getView(j, null, listView);
                listItem.measure(0, 0);
                int measuredHeight = listItem.getMeasuredHeight();
                result += measuredHeight;
            }
        }
        if (result > getScreenHeight() / 2) {
            //高度超过屏幕高度一半，设置高度为屏幕高度的一半
            result = getScreenHeight() / 2;
        }
        Log.e("TAG", "result--->" + result);
        return result;
    }

    /**
     * 如果列表用的recyclerView 通过该方法设置它显示的高度
     * 具体的测量在recyclerView设置setLayoutManager是重写onMeasure方法
     * 如果超过屏幕高度的1/2，显示屏幕高度的1/2 如果没有就正常显示
     *
     * @param height
     */
    public void setRecyclerViewHeight(int height) {
        if (height > getScreenHeight() / 2) {
            this.recyclerViewHeight = getScreenHeight() / 2;
        } else {
            this.recyclerViewHeight = height;
        }
    }

    /**
     * 获取屏幕的高度
     *
     * @return
     */
    private int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (currentTabPosition != -1) {
            TextView childAt = (TextView) tabMenuView.getChildAt(currentTabPosition);
            childAt.setTextColor(mTextUnSelectColor);
            childAt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(mMenuUnSelectIcon), null);
            popupMenuViews.setVisibility(View.GONE);
            popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
            maskView.setVisibility(View.GONE);
            maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
            currentTabPosition = -1;
        }
    }

    /**
     * 添加顶部tab
     *
     * @param tabTextList
     * @param index
     */
    private void addTab(List<String> tabTextList, int index) {
        final TextView tabTextView = new TextView(getContext());
        tabTextView.setSingleLine();
        tabTextView.setEllipsize(TextUtils.TruncateAt.END);
        tabTextView.setGravity(Gravity.CENTER);
        tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mMenuTextSize);
        tabTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        tabTextView.setTextColor(mTextUnSelectColor);
        tabTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(mMenuUnSelectIcon), null);
        tabTextView.setText(tabTextList.get(index));
        tabTextView.setPadding(dip2px(5f), dip2px(12f), dip2px(5f), dip2px(12f));
        //设置点击事件
        tabTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDividerShow){
                    switchTabContainLineMenu(tabTextView);
                }else{
                    switchTabMenu(tabTextView);
                }
            }
        });
        tabMenuView.addView(tabTextView);
        if(mDividerShow){
            //添加分割线
            if (index < tabTextList.size() - 1) {
                View underlineView = new View(getContext());
                LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(dip2px(0.5f), LayoutParams.MATCH_PARENT);
                underlineView.setBackgroundColor(mDividerColor);
                underlineView.setLayoutParams(lineLp);
                tabMenuView.addView(underlineView);
            }
        }
    }

    /**
     * 切换菜单
     * 顶部tab栏中间没有分割线
     * @param tragetView
     */
    private void switchTabMenu(TextView tragetView){
        int childCount = tabMenuView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //i = i + 2 原因是顶部tab 中间加了分割线 如果没有分割线还是正常的i++
            if (tragetView == tabMenuView.getChildAt(i)) {
                if (currentTabPosition == i) {
                    //关闭菜单
                    closeMenu();
                } else {
                    //弹出菜单
                    if (currentTabPosition == -1) {
                        //初始状况
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(View.VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        //i / 2的原因是顶部tab中加了分割线 如果没有分割线就直接是i
                        popupMenuViews.getChildAt(i).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                    currentTabPosition = i;
                    TextView childAt = (TextView) tabMenuView.getChildAt(i);
                    childAt.setTextColor(mTextSelectColor);
                    childAt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(mMenuSelectIcon), null);
                }
            } else {
                TextView childAt = (TextView) tabMenuView.getChildAt(i);
                childAt.setTextColor(mTextUnSelectColor);
                childAt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(mMenuUnSelectIcon), null);
                popupMenuViews.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }
    /**
     * 切换菜单
     * 顶部tab栏中间有分割线
     * @param tragetView
     */
    private void switchTabContainLineMenu(TextView tragetView) {
        int childCount = tabMenuView.getChildCount();
        for (int i = 0; i < childCount; i = i + 2) {
            //i = i + 2 原因是顶部tab 中间加了分割线 如果没有分割线还是正常的i++
            if (tragetView == tabMenuView.getChildAt(i)) {
                if (currentTabPosition == i) {
                    //关闭菜单
                    closeMenu();
                } else {
                    //弹出菜单
                    if (currentTabPosition == -1) {
                        //初始状况
                        popupMenuViews.setVisibility(View.VISIBLE);
                        popupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        maskView.setVisibility(View.VISIBLE);
                        maskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        //i / 2的原因是顶部tab中加了分割线 如果没有分割线就直接是i
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    } else {
                        popupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    }
                    currentTabPosition = i;
                    TextView childAt = (TextView) tabMenuView.getChildAt(i);
                    childAt.setTextColor(mTextSelectColor);
                    childAt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(mMenuSelectIcon), null);
                }
            } else {
                TextView childAt = (TextView) tabMenuView.getChildAt(i);
                childAt.setTextColor(mTextUnSelectColor);
                childAt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(mMenuUnSelectIcon), null);
                popupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    /**
     * DropDownMenu是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return currentTabPosition != -1;
    }

    /**
     * 设置选中的tab文字
     *
     * @param text
     */
    public void setTabText(String text) {
        TextView childAt = (TextView) tabMenuView.getChildAt(currentTabPosition);
        childAt.setText(text);
    }

    private int dip2px(float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
}
