package com.yunkairichu.cike.widget;

import com.yunkairichu.cike.bean.BaseResponseUserChainInfo;

/**
 * Created by haowenliang on 15/6/3.
 */
public class ChatListItemModel {
    public boolean isFemale;
    public boolean isLocalTmp;
    public BaseResponseUserChainInfo baseResponseUserChainInfo;

    public ChatListItemModel(boolean isLocalTmp, BaseResponseUserChainInfo baseResponseUserChainInfo){
        if(baseResponseUserChainInfo!=null){
            int userConfig = baseResponseUserChainInfo.getTitleInfo().getUserConfig();
            int sex = (userConfig & 1)>>0;
            this.isFemale = sex==0?false:true;
        } else {
            this.isFemale = true;
        }
        this.isLocalTmp = isLocalTmp;
        this.baseResponseUserChainInfo = baseResponseUserChainInfo;
    }
}
