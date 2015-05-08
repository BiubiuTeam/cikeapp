package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class PostRegister extends BasePostField {
    private String dvcInfo;
    private int userConfig;

    public String getDvcInfo() {
        return dvcInfo;
    }

    public void setDvcInfo(String dvcInfo) {
        this.dvcInfo = dvcInfo;
    }

    public int getUserConfig() {
        return userConfig;
    }

    public void setUserConfig(int userConfig) {
        this.userConfig = userConfig;
    }
}
