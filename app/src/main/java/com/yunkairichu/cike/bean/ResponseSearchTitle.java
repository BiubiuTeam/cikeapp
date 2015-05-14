package com.yunkairichu.cike.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vida2009 on 2015/5/7.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseSearchTitle implements Serializable {
    private String statusInfo;
    private int statusCode;
    private ReturnData returnData;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReturnData implements Serializable {
        private String city;
        private int lineLen;
        private int lineNum;
        private ArrayList<BaseResponseTitleInfo> contData;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getLineLen() {
            return lineLen;
        }

        public void setLineLen(int lineLen) {
            this.lineLen = lineLen;
        }

        public int getLineNum() {
            return lineNum;
        }

        public void setLineNum(int lineNum) {
            this.lineNum = lineNum;
        }

        public ArrayList<BaseResponseTitleInfo> getContData() {
            return contData;
        }

        public void setContData(ArrayList<BaseResponseTitleInfo> contData) {
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
