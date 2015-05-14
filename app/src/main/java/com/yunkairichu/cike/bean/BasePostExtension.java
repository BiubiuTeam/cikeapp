package com.yunkairichu.cike.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by vida2009 on 2015/5/7.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasePostExtension implements Serializable {
    private int voiceTimeLen;
    private int picSize;

    public int getVoiceTimeLen() {
        return voiceTimeLen;
    }

    public void setVoiceTimeLen(int voiceTimeLen) {
        this.voiceTimeLen = voiceTimeLen;
    }

    public int getPicSize() {
        return picSize;
    }

    public void setPicSize(int picSize) {
        this.picSize = picSize;
    }
}
