package com.yunkairichu.cike.main;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by liuxiaobo on 15/6/1.
 */
public class HalfCircleListView extends ListView implements AbsListView.OnScrollListener {
    //设置类似ios的listview下拉阻尼效果
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 0;
    private int mMaxYOverscrollDistance;

    private int adapterCount = 0;

    private boolean userOperation = false;

    private Context superContext;

    public HalfCircleListView(Context context) {
        super(context);
        setOnScrollListener(this);
        initBounceListView();
        this.superContext = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

        int first = absListView.getFirstVisiblePosition();
        int count = absListView.getChildCount();
        if(userOperation && (scrollState == SCROLL_STATE_IDLE || (first + count > adapterCount) )) {//冗余判断listview是否到了最末端
            ToolLog.dbg("SCROLL_STATE_IDLE ");
            //center list view when scroll state finish
//            absListView.setSelection(first);
            userOperation = false;
            ActivityChatview chatview = (ActivityChatview)this.superContext;
            chatview.centeralListViewAtIndex(first);
        }
        if(scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING){
            userOperation = true;
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        //Part of the magic happens here
        absListView.invalidateViews();
    }

    private void initBounceListView()
    {

        final DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        this.adapterCount = adapter.getCount();
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
    {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }

}
