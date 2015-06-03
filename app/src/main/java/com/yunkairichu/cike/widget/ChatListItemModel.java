package com.yunkairichu.cike.widget;

/**
 * Created by haowenliang on 15/6/3.
 */
public class ChatListItemModel {
    public Object dataModel;
    public boolean isFemale;
    public boolean isLocalTmp;

    public ChatListItemModel(Object data, boolean isFemale, boolean isLocalTmp){
        this.dataModel = data;
        this.isFemale = isFemale;
        this.isLocalTmp = isLocalTmp;
    }
}
