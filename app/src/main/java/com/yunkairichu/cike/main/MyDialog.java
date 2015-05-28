//package com.yunkairichu.cike.main;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.yunkairichu.cike.adapter.MyGridAdapter;
//import com.yunkairichu.cike.adapter.PictureAdapter;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by liuxiaobo on 15/5/28.
// */
//public class MyDialog extends Dialog{
//
//    private Context context;
//    private GridView gv = null;
//    private String str;
//    private int position;
//
//    //////////////////////////////////////////图片资源//////////////////////////////////////////////////
//    private int[] statusId = new int[]{R.drawable.status000,R.drawable.status001,R.drawable.status002,R.drawable.status003
//            ,R.drawable.status004,R.drawable.status005,R.drawable.status006,R.drawable.status007,R.drawable.status008};
//    private String[] statusName= new String[]{"失恋中","无聊","思考人生","上自习","在路上","上班ing","健身","吃大餐","自拍"};
//    private GridView gv;
//    HashMap<String,Integer> statusMap=null;
//    ArrayList<ArrayList<HashMap<String,Object>>> listGrid=null;
//    private RelativeLayout mystatus;
//
//    public MyDialog(Context context) {
//        super(context);
//        this.context = context;
//        // TODO Auto-generated constructor stub
//    }
//
//    public MyDialog(Context context, int theme, String str) {
//        super(context, theme);
//        this.context = context;
//        this.str = str;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        // 设置对话框使用的布局文件
//        this.setContentView(R.layout.status_picker);
//
//        // 设置GridView的数据源
//        // 设置GridView的数据源
////        SimpleAdapter adapter = new SimpleAdapter(context,//当前View
////                getPriorityList(str), //数据源
////                R.layout.status_grid_item,//item的布局文件
////                new String[] { "list_value" },
////                new int[] { R.id.itemText });//布局文件里面对应的view的id
////        dlg_grid.setAdapter(adapter);
////        dlg_grid.setAdapter(adapter);
//
//
//        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Toast.makeText(ActivitySquare.this, "pic" + (position + 1), Toast.LENGTH_SHORT).show();
//            }
//        });
//
////        // 为GridView设置监听器
////        dlg_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
////                                    long arg3) {
////                // TODO Auto-generated method stub
////                Toast.makeText(context, "" + arg2, Toast.LENGTH_SHORT).show();// 显示信息;
////                position = arg2;
////                //点击item后Dialog消失
////                PriorityDlg.this.dismiss();
////
////            }
////        });
//
//
//        /**
//         * 为状态Map添加数据
//         */
//        for (int i = 0; i < statusId.length; i++) {
//            statusMap.put(statusName[i], statusId[i]);
//        }
//
//        addStatusData();
//        addStatusGridView();
//    }
//
//    private void addStatusData() {
//        ArrayList<HashMap<String, Object>> list = null;
//        for (int i = 0; i < statusId.length; i++) {
//            list = new ArrayList<HashMap<String, Object>>();
//            listGrid.add(list);
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            map.put("statusImage", statusId[i]);
//            map.put("statusName", statusName[i]);
//        }
//    }
//
//    private void addStatusGridView() {
//        for (int i = 0; i < listGrid.size(); i++) {
//            View view = LayoutInflater.from(this).inflate(R.layout.status_view_item, null);
//            GridView gv = (GridView) view.findViewById(R.id.myStatusGridView);
//            gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
//            MyGridAdapter adapter = new MyGridAdapter(this, listGrid.get(i), R.layout.status_grid_item, new String[]{"statusImage"}, new String[]{"statusName"}, new int[]{R.id.stausGridImage},new int[]{R.id.statusGridText});
//            gv.setAdapter(adapter);
//        }
//
//    }
//
//}
