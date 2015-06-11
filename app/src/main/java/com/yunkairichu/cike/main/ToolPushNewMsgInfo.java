package com.yunkairichu.cike.main;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.bean.BaseResponseUserChainInfo;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseUserChainPull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vida2009 on 2015/6/10.
 */
public class ToolPushNewMsgInfo {
    private JSONObject titleNewMsgFlag = new JSONObject();
    private int titleHasNewMsgCnt = 0;

    /**
     * 内部类实现单例模式
     * 延迟加载，减少内存开销
     *
     * @author xuzhaohu
     *
     */
    private static class ToolPushNewMsgInfoHolder {
        private static ToolPushNewMsgInfo instance = new ToolPushNewMsgInfo();
    }

    /**
     * 私有的构造函数
     */
    private ToolPushNewMsgInfo() {

    }

    public static ToolPushNewMsgInfo getInstance() {
        return ToolPushNewMsgInfoHolder.instance;
    }

    protected void method() {
        System.out.println("SingletonInner");
    }

    public void initTitleNewMsg(){
        titleNewMsgFlag = new JSONObject();
        titleNewMsgCheck();
    }

    //注意，这里暂时只是缓存30条的
    public void titleNewMsgCheck() {
        Http http = new Http();
        JSONObject jo = JsonPack.buildUserChainPull(1, 0);
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                //入列表
                ResponseUserChainPull responseUserChainPull = (JacksonWrapper.json2Bean(response, ResponseUserChainPull.class));
                JSONObject checkUnreadJson = new JSONObject();
                int i;
                for (i = 0; i < responseUserChainPull.getReturnData().getContData().size(); i++) {
                    BaseResponseUserChainInfo baseResponseUserChainInfo = responseUserChainPull.getReturnData().getContData().get(i);
                    try {
                        String key = String.valueOf(baseResponseUserChainInfo.getTitleInfo().getTitleId())+baseResponseUserChainInfo.getTitleInfo().getDvcId();
                        if (checkUnreadJson.isNull(baseResponseUserChainInfo.getTitleInfo().getDvcId()) || checkUnreadJson.get(baseResponseUserChainInfo.getTitleInfo().getDvcId()) == 0) {
                            checkUnreadJson.put(baseResponseUserChainInfo.getTitleInfo().getDvcId(), 1);
                            EMConversation conversation = EMChatManager.getInstance().getConversation(baseResponseUserChainInfo.getTitleInfo().getDvcId());
                            titleNewMsgFlag.put(key, conversation.getUnreadMsgCount());
                            //ToolLog.dbg("AtitleId:" + key + " Avalues:" + titleNewMsgFlag.get(key));
                            if (conversation.getUnreadMsgCount() > 0) {
                                titleHasNewMsgCnt++;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public int getTitleNewMsgFlagValue(String key){
        ToolLog.dbg("key:"+key);
        try {
            if(titleNewMsgFlag.isNull(key) || (int)titleNewMsgFlag.get(key) <= 0) {
                return 0;
            } else {
                return (int)titleNewMsgFlag.get(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void putTitleNewMsgFlagValue(String key, int value){
        try {
            if(value > 0) {
                if (titleNewMsgFlag.isNull(key) || (int) titleNewMsgFlag.get(key) <= 0) {
                    titleHasNewMsgCnt++;
                }
            } else {
                if (titleNewMsgFlag.isNull(key) && (int) titleNewMsgFlag.get(key) > 0) {
                    if(titleHasNewMsgCnt>0){
                        titleHasNewMsgCnt--;
                    }
                }
            }
            titleNewMsgFlag.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getTitleHasNewMsgCnt(){
        return titleHasNewMsgCnt;
    }

    public void setTitleHasNewMsgCnt(int titleHasNewMsgCnt){
        this.titleHasNewMsgCnt = titleHasNewMsgCnt;
    }

    public void titleHasNewMsgCntAddOne(){
        titleHasNewMsgCnt++;
    }

    public void titleHasNewMsgCntMinusOne(){
        if(titleHasNewMsgCnt > 0) {
            titleHasNewMsgCnt--;
        }
    }
}
