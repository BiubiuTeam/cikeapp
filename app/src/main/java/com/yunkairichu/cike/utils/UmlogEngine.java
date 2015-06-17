package com.yunkairichu.cike.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by haowenliang on 15/6/17.
 */
public class UmlogEngine {

    public enum  LogEvent{
        PublishStatus,  //发布状态,描述发布状态被点击的次数和独立用户数
        PickStatus, //筛选状态,筛选状态被发起的次数和独立用户数
        ViewStatus, //查看信息详情,信息被查看的次数
        StartChat,  //发起聊天,成功发起的对话个数和独立用户数（以一个对话中产生一条信息为准）
        ViewChat,   //查看过往聊天,聊天入口被点击的次数和独立用户数
        Browse, //浏览信息广场,加载新信息的请求数和独立用户数（一次请求拉3个页面布局）
        PressureMode   //宣泄模式,进入宣泄模式的次数和独立用户数
    };

    public enum ViewType{
        LongPress,
        Click,
    };

    public enum ContentType{
        text,
        photo,
    };

    public enum LocationType{
        Global,
        City,
        Nearby,
    };

    public enum UserType{
        all,
        male,
        female,
        student,
        worker,
    };

    private String[] StatusType = {"无聊","失恋中","思考人生","上自习","等车","上班ing","看b站","健身","吃大餐","在自拍","拉粑粑"};

    private static UmlogEngine ourInstance = new UmlogEngine();

    public static UmlogEngine getInstance() {
        return ourInstance;
    }

    private UmlogEngine() {
    }

    /**
     * 统计发生次数
     * @param context
     * @param eventId
     */
    public void onUmlogEventCount(Context context, String eventId){
        MobclickAgent.onEvent(context, eventId);
    }

    /**
     * 統計事件次數
     * @param context
     * @param logevent
     */
    public void onUmlogLogEvent(Context context, LogEvent logevent){
        MobclickAgent.onEvent(context, logevent.toString());
    }
    /**
     * 统计点击行为各属性被触发的次数
     * @param context
     * @param eventId
     * @param map
     */
    public void onUmlogEventMap(Context context, String eventId, HashMap<String,String> map){
        MobclickAgent.onEvent(context, eventId, map);
    }

    /**
     * 统计点击行为各属性被触发的次数
     * @param context
     * @param logevent
     * @param map
     */
    public void onUmlogLogEventMap(Context context, LogEvent logevent, HashMap<String,String> map){
        MobclickAgent.onEvent(context, logevent.toString(), map);
    }
}
