package com.yunkairichu.cike.main;

/**
 * Created by vida2009 on 2015/5/8.
 */
public class ToolLog {
    private static final String TAG = "bbut";
    private static final boolean DBG = true;

    public static void dbg(String s) {
        if(DBG) android.util.Log.e(TAG, s);
    }

    public static void dbg(String f, Object... args) {
        dbg(String.format(f, args));
    }
}
