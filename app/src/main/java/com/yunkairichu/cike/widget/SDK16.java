package com.yunkairichu.cike.widget;

import android.annotation.TargetApi;
import android.view.View;

/**
 * Created by vida2009 on 2015/5/21.
 */
@TargetApi(16)
public class SDK16 {

    public static void postOnAnimation(View view, Runnable r) {
        view.postOnAnimation(r);
    }

}
