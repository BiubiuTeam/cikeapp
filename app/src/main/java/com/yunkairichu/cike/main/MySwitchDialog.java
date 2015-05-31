//package com.yunkairichu.cike.main;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.GridView;
//import android.widget.SimpleAdapter;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by liuxiaobo on 15/5/29.
// */
//public class MySwitchDialog extends Dialog {
//    private Context context;
//    private GridView gv1 = null;GridView gv2 = null;GridView gv3 = null;
//    private String str;
//    private int position;
//
//
//    private String[] statusName = new String[]{"全部","失恋中", "无聊", "思考人生", "上自习", "在路上", "上班ing", "健身", "吃大餐", "自拍"};
//    private String[] scale = new String[]{"全球","同城","身边"};
//    private String[] gender = new String[]{"所有人","男生","女生"};
//    HashMap<String,Integer> statusNameMap=null;
//    HashMap<String,Integer> scaleMap=null;
//    HashMap<String,Integer> genderMap=null;
//
//    public MySwitchDialog(Context context) {
//        super(context);
//        this.context = context;
//        // TODO Auto-generated constructor stub
//    }
//
//    public MySwitchDialog(Context context, int theme, String str) {
//        super(context, theme);
//        this.context = context;
//        this.str = str;
//    }
//
//    public MySwitchDialog(Context context, int theme) {
//        this(context, theme, null);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        // 设置对话框使用的布局文件
//        View root = getWindow().getDecorView();
////        this.setContentView(R.layout.status_picker);
//        gv1 = (GridView) root.findViewById(R.id.statusscale);
//        gv2 = (GridView) root.findViewById(R.id.statustype);
//        gv3 = (GridView) root.findViewById(R.id.usergender);
//
//        ArrayList<HashMap<String, Object>> statusNameList = new ArrayList<HashMap<String, Object>>();
//
//        for (int i = 0; i <statusName.length ; i++) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("status", statusName[i]);
//            statusNameList.add(map);
//        }
//
//        ArrayList<HashMap<String, Object>> scaleList = new ArrayList<HashMap<String, Object>>();
//
//        for (int i = 0; i <scale.length ; i++) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("scale", scale[i]);
//            scaleList.add(map);
//        }
//
//        ArrayList<HashMap<String, Object>> genderList = new ArrayList<HashMap<String, Object>>();
//        for (int i = 0; i <gender.length ; i++) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("gender", gender[i]);
//            genderList.add(map);
//        }
//
//        // 设置GridView的数据源
//        SimpleAdapter simpleAdapter1 = new SimpleAdapter(this.context, scaleList,
//                R.layout.scale_item, new String[] { "scale" }, new int[] {
//                 R.id.scale_item });
//        gv1.setAdapter(simpleAdapter1);
//
//        SimpleAdapter simpleAdapter2 = new SimpleAdapter(this.context, statusNameList,
//                R.layout.status_selector_item, new String[] { "status" }, new int[] {
//                R.id.status_text });
//        gv2.setAdapter(simpleAdapter2);
//
//        SimpleAdapter simpleAdapter3 = new SimpleAdapter(this.context, genderList,
//                R.layout.gender_item, new String[] { "gender" }, new int[] {
//                R.id.gender_item });
//        gv3.setAdapter(simpleAdapter3);
//
//        gv1.setSelector(new ColorDrawable(Color.TRANSPARENT));
//        gv2.setSelector(new ColorDrawable(Color.TRANSPARENT));
//        gv3.setSelector(new ColorDrawable(Color.TRANSPARENT));
//
//
//    }
//
//
//}
