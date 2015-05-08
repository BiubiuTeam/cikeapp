package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class BaseResponseUserChainInfo {
    private long sortId;
    private BaseResponseTitleInfo titleInfo;

    public long getSortId() {
        return sortId;
    }

    public void setSortId(long sortId) {
        this.sortId = sortId;
    }

    public BaseResponseTitleInfo getTitleInfo() {
        return titleInfo;
    }

    public void setTitleInfo(BaseResponseTitleInfo titleInfo) {
        this.titleInfo = titleInfo;
    }
}
