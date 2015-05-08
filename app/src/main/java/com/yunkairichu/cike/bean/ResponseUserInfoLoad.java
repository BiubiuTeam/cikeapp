package com.yunkairichu.cike.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by vida2009 on 2015/5/7.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUserInfoLoad {
    private int statusCode;
    private String statusInfo;
    private BaseResponseUserInfo returnData;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    public BaseResponseUserInfo getReturnData() {
        return returnData;
    }

    public void setReturnData(BaseResponseUserInfo returnData) {
        this.returnData = returnData;
    }
}
