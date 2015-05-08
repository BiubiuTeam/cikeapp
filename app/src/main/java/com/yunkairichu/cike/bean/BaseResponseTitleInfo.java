package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class BaseResponseTitleInfo {
    private long sortId;
    private long titleId;
    private String dvcId;
    private int userConfig;
    private String city;
    private String titleCont;
    private int titleType;
    private int pubTime;
    private double latitude;
    private double longitude;
    private BasePostExtension extension;
    private int blocklen;
    private int msgTag;

    public long getSortId() {
        return sortId;
    }

    public void setSortId(long sortId) {
        this.sortId = sortId;
    }

    public long getTitleId() {
        return titleId;
    }

    public void setTitleId(long titleId) {
        this.titleId = titleId;
    }

    public String getDvcId() {
        return dvcId;
    }

    public void setDvcId(String dvcId) {
        this.dvcId = dvcId;
    }

    public int getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(int userConfig) {
        this.userConfig = userConfig;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitleCont() {
        return titleCont;
    }

    public void setTitleCont(String titleCont) {
        this.titleCont = titleCont;
    }

    public int getTitleType() {
        return titleType;
    }

    public void setTitleType(int titleType) {
        this.titleType = titleType;
    }

    public int getPubTime() {
        return pubTime;
    }

    public void setPubTime(int pubTime) {
        this.pubTime = pubTime;
    }

    public double getLatitude() {
        return (double) (latitude / 1E6);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return (double) (longitude / 1E6);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public BasePostExtension getExtension() {
        return extension;
    }

    public void setExtension(BasePostExtension extension) {
        this.extension = extension;
    }

    public int getBlocklen() {
        return blocklen;
    }

    public void setBlocklen(int blocklen) {
        this.blocklen = blocklen;
    }

    public int getMsgTag() {
        return msgTag;
    }

    public void setMsgTag(int msgTag) {
        this.msgTag = msgTag;
    }
}
