package com.yunkairichu.cike.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.main.ToolLog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by haowenliang on 15/6/16.
 */
public class StatusSelectorLayout extends RelativeLayout{

    private Context linkContext;

    private GridView gv1 = null;
    GridView gv2 = null;
    GridView gv3 = null;

    private SimpleAdapter simpleAdapter1;
    private SimpleAdapter simpleAdapter2;
    private SimpleAdapter simpleAdapter3;

    private static int selectorStatus = 0;
    private static int selectorScale = 0;
    private static int selectorGender = 0;

    private int tmpStatus = 0;
    private int tmpScale = 0;
    private int tmpGender = 0;

    private String[] statusName = new String[]{"全部","失恋中", "无聊", "思考人生", "上自习", "在路上", "上班ing", "健身", "吃大餐", "自拍"};
    private String[] scale = new String[]{"全球","同城","身边"};
    private String[] gender = new String[]{"所有人","男生","女生"};

    private int[] statusNameNum = {0,1,2,3,4,5,6,8,9,10};
    private int[] scaleNum = {0,1,1};
    private int[] genderNum ={0,1,2};

    public StatusSelectorLayout(Context context) {
        super(context);
        linkContext = context;
    }

    public StatusSelectorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        linkContext = context;
    }

    public StatusSelectorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        linkContext = context;
    }

    public StatusSelectorLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        linkContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initBaseDatas();
        initSelectorView();
    }

    private void initBaseDatas(){
        ArrayList<HashMap<String, Object>> statusNameList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i <statusName.length ; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("status", statusName[i]);
            statusNameList.add(map);
        }

        ArrayList<HashMap<String, Object>> scaleList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i <scale.length ; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("scale", scale[i]);
            scaleList.add(map);
        }

        ArrayList<HashMap<String, Object>> genderList = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i <gender.length ; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("gender", gender[i]);
            genderList.add(map);
        }

        // 设置GridView的数据源
        simpleAdapter1 = new SimpleAdapter(linkContext, scaleList,
                R.layout.scale_item, new String[] { "scale" }, new int[] {
                R.id.scale_item });

        simpleAdapter2 = new SimpleAdapter(linkContext, statusNameList,
                R.layout.status_selector_item, new String[] { "status" }, new int[] {
                R.id.status_text });

        simpleAdapter3 = new SimpleAdapter(linkContext, genderList,
                R.layout.gender_item, new String[] { "gender" }, new int[] {
                R.id.gender_item });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void initSelectorView()
    {
        gv1 = (GridView)findViewById(R.id.statusscale);
        gv2 = (GridView)findViewById(R.id.statustype);
        gv3 = (GridView)findViewById(R.id.usergender);

        gv1.setAdapter(simpleAdapter1);
        gv2.setAdapter(simpleAdapter2);
        gv3.setAdapter(simpleAdapter3);

        gv1.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv2.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv3.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //给查看区域的选项设置点击事件
        gv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                ((TextView)gv1.getChildAt(selectorScale).findViewById(R.id.scale_item)).setTextColor(0xffffffff);
                ((TextView)gv1.getChildAt(selectorScale).findViewById(R.id.scale_item)).setSelected(false);
                ToolLog.dbg("selectorScale:" + String.valueOf(selectorScale) + " position" + String.valueOf(position));

                selectorScale = position;
                v.setSelected(true);
                ((TextView)v.findViewById(R.id.scale_item)).setTextColor(0xff000000);
            }
        });

        //给查看状态的选项设置点击事件
        gv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                ((TextView)gv2.getChildAt(selectorStatus).findViewById(R.id.status_text)).setTextColor(0xffffffff);
                ((TextView)gv2.getChildAt(selectorStatus).findViewById(R.id.status_text)).setSelected(false);
                ToolLog.dbg("selectorStatus:"+String.valueOf(selectorStatus)+" position"+String.valueOf(position));

                selectorStatus = position;
                v.setSelected(true);
                ((TextView)v.findViewById(R.id.status_text)).setTextColor(0xff000000);
            }
        });

        //给查看性别的选项设置点击事件
        gv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> contentView, View v,
                                    int position, long id) {
                ((TextView)gv3.getChildAt(selectorGender).findViewById(R.id.gender_item)).setTextColor(0xffffffff);
                ((TextView)gv3.getChildAt(selectorGender).findViewById(R.id.gender_item)).setSelected(false);
                ToolLog.dbg("selectorGender:" + String.valueOf(selectorGender) + " position" + String.valueOf(position));

                selectorGender = position;
                v.setSelected(true);
                ((TextView)v.findViewById(R.id.gender_item)).setTextColor(0xff000000);
            }
        });
    }

    //interface methods to outside
    public void storeLastSelection(){
        tmpGender = selectorGender;
        tmpScale = selectorScale;
        tmpStatus = selectorStatus;

        //base init color
        TextView tmpTV =((TextView)gv1.getChildAt(selectorScale).findViewById(R.id.scale_item));
        tmpTV.setTextColor(0xff000000);
        tmpTV.setSelected(true);

        tmpTV = ((TextView)gv2.getChildAt(selectorStatus).findViewById(R.id.status_text));
        tmpTV.setTextColor(0xff000000);
        tmpTV.setSelected(true);

        tmpTV = ((TextView)gv3.getChildAt(selectorGender).findViewById(R.id.gender_item));
        tmpTV.setTextColor(0xff000000);
        tmpTV.setSelected(true);

    }

    public String getSelectedStatusWord(){
        String word = "";
        word = word+scale[selectorScale]+"."+statusName[selectorStatus]+"."+gender[selectorGender];
        return word;
    }

    public int getSelectedStatusFilter(){
        int filter = 0;
        filter |= (scaleNum[selectorScale]<<0);
        filter |= (genderNum[selectorGender]<<1);
        return filter;
    }

    public int getSelectorStatusNum(){
        return statusNameNum[selectorStatus];
    }

    public boolean contentChanged(){
        if(tmpGender != selectorGender || tmpStatus != selectorStatus || tmpScale != selectorScale)
            return true;
        return false;
    }
}
