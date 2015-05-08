package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class PostSearchTitle extends BasePostField {
    private int filter;
    private int msgTag;

    public int getFilter() {
        return filter;
    }

    public void setFilter(int userConfig) {
        this.filter = filter;
    }

    public int getMsgTag() {
        return msgTag;
    }

    public void setMsgTag(int msgTag) {
        this.msgTag = msgTag;
    }
}
