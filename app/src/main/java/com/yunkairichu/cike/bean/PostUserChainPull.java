package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class PostUserChainPull extends BasePostField{
    private int IdType;
    private long lastId;

    public int getIdType() {
        return IdType;
    }

    public void setIdType(int IdType) {
        this.IdType = IdType;
    }

    public long getLastId() {
        return lastId;
    }

    public void setLastId(long lastId) {
        this.lastId = lastId;
    }
}
