package com.yunkairichu.cike.bean;

import java.io.Serializable;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class BasePostField implements Serializable {
    private int cmd;
    private String appVersion;
    private String platform;
    private String dvcId;
    private int latitude;
    private int longitude;
    private String city;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDvcId() {
        return dvcId;
    }

    public void setDvcId(String dvcId) {
        this.dvcId = dvcId;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = (int) (latitude * 1E6);
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = (int) (longitude * 1E6);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
