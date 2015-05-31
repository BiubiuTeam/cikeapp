package com.yunkairichu.cike.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import com.yunkairichu.cike.adapter.PictureAdapter;

/**
 * Created by liuxiaobo on 15/5/28.
 */
public class MyDialog extends Dialog {

    private Context context;
    private GridView gv = null;
    private String str;
    private int position;
    private Activity activity;

    //////////////////////////////////////////图片资源//////////////////////////////////////////////////
    private int[] statusId = new int[]{R.drawable.status000, R.drawable.status001, R.drawable.status002, R.drawable.status003
            , R.drawable.status004, R.drawable.status005, R.drawable.status006, R.drawable.status007, R.drawable.status008};
    private String[] statusName = new String[]{"失恋中", "无聊", "思考人生", "上自习", "在路上", "上班ing", "健身", "吃大餐", "自拍"};

    public MyDialog(Context context) {
        super(context);
        this.context = context;
        // TODO Auto-generated constructor stub
        activity = (Activity) context;
    }

    public MyDialog(Context context, int theme, String str) {
        super(context, theme);
        this.context = context;
        this.str = str;
        activity = (Activity) context;
    }

    public MyDialog(Context context, int theme) {
        this(context, theme, null);
        activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // 设置对话框使用的布局文件
        View root = getWindow().getDecorView();
//        this.setContentView(R.layout.status_picker);
        gv = (GridView) root.findViewById(R.id.myStatusGridView);

        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 设置GridView的数据源
        PictureAdapter adapter = new PictureAdapter(statusName, statusId, this.context, activity);
        gv.setAdapter(adapter);

    }
}

