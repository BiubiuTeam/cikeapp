package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class PostUserChainInsert extends BasePostField{
    private String toDvcId;
    private long titleId;

    public String getToDvcId() {
        return toDvcId;
    }

    public void setToDvcId(String toDvcId) {
        this.toDvcId = toDvcId;
    }

    public long getTitleId() {
        return titleId;
    }

    public void setTitleId(long titleId) {
        this.titleId = titleId;
    }
}
