package com.yunkairichu.cike.main;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jaf.jcore.Application;

/**
 * Created by vida2009 on 2015/5/8.
 */
public class ToolDevice {
    static public int dw;//屏幕宽度
    public static int dh;//屏幕高度

    //统一转小写
    public static String getId(Context context) {
        String aid = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return aid.toLowerCase();
    }

    public static float dp2px(float dp) {
        Resources r = Application.getInstance().getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    public static float sp2px(float sp) {
        Resources r = Application.getInstance().getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, r.getDisplayMetrics());
        return px;
    }

    public static void showSoftKeyboard(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public  static int getDw(){
        DisplayMetrics dm = new DisplayMetrics();
        dw = dm.widthPixels;
        return dw;
    }

    public static int getDh(){
        DisplayMetrics dm = new DisplayMetrics();
        dh = dm.heightPixels;
        return dh;
    }

}
