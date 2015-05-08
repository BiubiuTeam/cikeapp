package com.yunkairichu.cike.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by vida2009 on 2015/5/7.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUserChainPull {
    private String statusInfo;
    private int statusCode;
    private ReturnData returnData;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReturnData {
        private ArrayList<BaseResponseUserChainInfo> contData;

        public ArrayList<BaseResponseUserChainInfo> getContData() {
            return contData;
        }

        public void setContData(ArrayList<BaseResponseUserChainInfo> contData) {
            this.contData = contData;
        }
    }

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

    public ReturnData getReturnData() {
        return returnData;
    }

    public void setReturnData(ReturnData returnData) {
        this.returnData = returnData;
    }
}
