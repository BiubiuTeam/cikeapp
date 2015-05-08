package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class BaseResponseUserInfo {
    private int userConfig;
    private String city;
    private int latitude;
    private int longitude;

    public int getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(int userConfig) {
        this.userConfig = userConfig;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
