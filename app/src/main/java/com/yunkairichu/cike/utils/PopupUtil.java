package com.yunkairichu.cike.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.yunkairichu.cike.main.MyDialog;
import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.main.ToolDevice;

//import com.yunkairichu.cike.main.MySwitchDialog;

/**
 * 需要在style里面添加这些xml
 <style name="XDialog" parent="@android:style/Theme.Dialog">
 <item name="android:windowBackground">@android:color/transparent</item>
 <item name="android:windowFrame">@null</item>
 <item name="android:windowNoTitle">true</item>
 <item name="android:windowIsFloating">true</item>
 <item name="android:windowIsTranslucent">true</item>
 <item name="android:windowAnimationStyle">@style/DialogAnimation</item>
 <item name="android:background">@android:color/transparent</item>
 <item name="android:backgroundDimEnabled">true</item>
 <item name="android:windowFullscreen">true</item>
 </style>

 <style name="DialogAnimation" parent="@android:style/Animation.Dialog">
 <item name="android:windowEnterAnimation">@anim/push_up_in</item>
 <item name="android:windowExitAnimation">@anim/push_down_out</item>
 </style>
 */

//需要添加这些动画
//push_down_out
//<?xml version="1.0" encoding="utf-8"?>
//<set xmlns:android="http://schemas.android.com/apk/res/android" >
//
//    <translate
//        android:duration="300"
//        android:fromYDelta="0.0"
//        android:toYDelta="100.0%p" />
//
//</set>

//push_up_in
//<?xml version="1.0" encoding="utf-8"?>
//<set xmlns:android="http://schemas.android.com/apk/res/android" >
//
//    <translate
//        android:duration="300"
//        android:fromYDelta="100.0%p"
//        android:toYDelta="0.0" />
//
//</set>



public class PopupUtil {

    public static final int WIDTH = 0;
    public static final int HEIGHT = 1;

    public static boolean showDialog(Dialog dialog) {

        if (dialog == null) {
            log("dialog is null");
            return false;
        }

        if (dialog.isShowing()) {
            log("dialog is showing");
            return false;
        }

        dialog.show();
        return true;
    }

    /**
     * create a popup menu
     * @param context
     * @param contentView
     * @return
     */
    public static Dialog makePopup(Context context, View contentView) {
        Dialog dialog = new Dialog(context, R.style.popupDialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        int[] size = getScreenSize(context);
        windowParams.x = 0;
        windowParams.y = size[HEIGHT];

        //设置window的布局参数
        window.setAttributes(windowParams);
        // window.setBackgroundDrawableResource(R.drawable.alert_dialog_background);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(contentView);
        // 显示的大小是contentView 的大小
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    /**
     * create a popup menu
     * @param context
     * @param contentView
     * @return
     */
    public static MyDialog makeMyPopup(Context context, View contentView) {
        MyDialog mydialog = new MyDialog(context, R.style.popupMyDialog);
        Window window = mydialog.getWindow();
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        int[] size = getScreenSize(context);
        windowParams.x = 0;
        windowParams.y = size[HEIGHT];

        //设置window的布局参数
        window.setAttributes(windowParams);
        // window.setBackgroundDrawableResource(R.drawable.alert_dialog_background);
        mydialog.setCanceledOnTouchOutside(true);
        mydialog.setContentView(contentView);
        // 显示的大小是contentView 的大小
        mydialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        return mydialog;
    }

    /**
     * create a popup menu
     * @param context
     * @param contentView
     * @return
     */
    public static Dialog makeToast(Context context, View contentView) {
        Dialog dialog = new Dialog(context, R.style.toastDialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        int[] size = getScreenSize(context);
        windowParams.gravity = Gravity.CENTER;
//        windowParams.x = 0;
//        windowParams.y = size[HEIGHT/2];

        //设置window的布局参数
        window.setAttributes(windowParams);
        // window.setBackgroundDrawableResource(R.drawable.alert_dialog_background);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(contentView);
        // 显示的大小是contentView 的大小
        dialog.getWindow().setLayout((int) ToolDevice.dp2px(350),
                LayoutParams.WRAP_CONTENT);
        return dialog;
    }


    /**
     * create a popup menu
     * @param context
     * @param contentView
     * @return
     */
//    public static MySwitchDialog makeMySwitchPopup(Context context, View contentView) {
//        MySwitchDialog mydialog = new MySwitchDialog(context, R.style.popupMyDialog);
//        Window window = mydialog.getWindow();
//        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
//        int[] size = getScreenSize(context);
//        windowParams.x = 0;
//        windowParams.y = (int) ToolDevice.dp2px(80.0f);
//
//        //设置window的布局参数
//        window.setAttributes(windowParams);
//        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
//        // window.setBackgroundDrawableResource(R.drawable.alert_dialog_background);
//        mydialog.setCanceledOnTouchOutside(true);
//        mydialog.setContentView(contentView);
//        // 显示的大小是contentView 的大小
//        mydialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT);
//        return mydialog;
//    }

    /**
     * get device size
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        int[] deviceSize = new int[2];
        int w = 0;
        int h = 0;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        w = dm.widthPixels;
        h = dm.heightPixels;
        deviceSize[WIDTH] = w;
        deviceSize[HEIGHT] = h;
        return deviceSize;
    }

    public static void log(String msg) {
        Log.e("dialog", msg);
    }
}
