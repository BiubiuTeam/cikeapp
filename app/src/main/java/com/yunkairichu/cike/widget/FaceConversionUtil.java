package com.yunkairichu.cike.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

import com.yunkairichu.cike.main.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by haowenliang on 15/6/9.
 * @文件描述 : 表情轉換工具
 */
public class FaceConversionUtil {
    /** 每一页表情的个数 */
    private int pageSize = 6;

    private static FaceConversionUtil mFaceConversionUtil;

    /** 保存于内存中的表情HashMap */
    private HashMap<String, Integer> emojiMap = new HashMap<String, Integer>();

    /** 保存于内存中的表情集合 */
    private List<ChatEmoji> emojis = new ArrayList<ChatEmoji>();

    /** 表情分页的结果集合 */
    public List<List<ChatEmoji>> emojiLists = new ArrayList<List<ChatEmoji>>();

    int[] faceId={R.drawable.f_static_000,R.drawable.f_static_001,R.drawable.f_static_002,R.drawable.f_static_003
            ,R.drawable.f_static_004,R.drawable.f_static_005,R.drawable.f_static_006,R.drawable.f_static_007,R.drawable.f_static_008,R.drawable.f_static_009,R.drawable.f_static_010,R.drawable.f_static_011};
    int[] flyFaceId={R.drawable.f_000,R.drawable.f_001,R.drawable.f_002,R.drawable.f_003
            ,R.drawable.f_004,R.drawable.f_005,R.drawable.f_006,R.drawable.f_007,R.drawable.f_008,R.drawable.f_009,R.drawable.f_010,R.drawable.f_011};
    String[] faceName={"鄙视","害羞","汗","抠鼻","哭","酷","吐舌头","笑","正直","窒息","醉了","Duang"};
    HashMap<String,Integer> faceMap=null;

    private FaceConversionUtil() {
        faceMap=new HashMap<String,Integer>();
        this.ParseData();
    }

    public static FaceConversionUtil getInstace() {
        if (mFaceConversionUtil == null) {
            mFaceConversionUtil = new FaceConversionUtil();
        }
        return mFaceConversionUtil;
    }

    /**
     * 解析字符
     */
    private void ParseData() {
        ChatEmoji emojEentry;
        try {
            int length = faceId.length;
            for (int i = 0; i < length; i++) {
                int resID = faceId[i];
                if (resID != 0) {
                    emojEentry = new ChatEmoji();
                    emojEentry.setId(resID);
                    emojEentry.setFaceName(faceName[i]);
                    emojEentry.setFlyEmojiId(flyFaceId[i]);
                    emojis.add(emojEentry);
                    emojiMap.put(faceName[i], faceId[i]);
                }
            }

            int pageCount = (int) Math.floor((emojis.size()-1) / pageSize) + 1;
            for (int i = 0; i < pageCount; i++) {
                emojiLists.add(getData(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分页数据
     *
     * @param page
     * @return
     */
    private List<ChatEmoji> getData(int page) {
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;

        if (endIndex > emojis.size()) {
            endIndex = emojis.size();
        }
        // 不这么写，会在viewpager加载中报集合操作异常，我也不知道为什么
        List<ChatEmoji> list = new ArrayList<ChatEmoji>();
        list.addAll(emojis.subList(startIndex, endIndex));
        if (list.size() < pageSize) {
            for (int i = list.size(); i < pageSize; i++) {
                ChatEmoji object = new ChatEmoji();
                list.add(object);
            }
        }
        return list;
    }
}
