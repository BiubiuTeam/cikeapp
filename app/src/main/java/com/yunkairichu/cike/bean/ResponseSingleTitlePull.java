package com.yunkairichu.cike.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by vida2009 on 2015/5/7.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseSingleTitlePull {
    private String statusInfo;
    private int statusCode;
    private BaseResponseTitleInfo returnData;

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public BaseResponseTitleInfo getReturnData() {
        return returnData;
    }

    public void setReturnData(BaseResponseTitleInfo returnData) {
        this.returnData = returnData;
    }
}
