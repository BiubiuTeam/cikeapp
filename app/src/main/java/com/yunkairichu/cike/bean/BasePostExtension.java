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
    private String text;
    private int msgTag;

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

    public int getMsgTag() {
        return msgTag;
    }

    public void setMsgTag(int msgTag) {
        this.msgTag = msgTag;
    }

    public String getText() {
        return text;
    }

    public void setText(String Text) {
        this.text = Text;
    }
}
