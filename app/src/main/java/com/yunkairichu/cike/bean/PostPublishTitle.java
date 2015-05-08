package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class PostPublishTitle extends BasePostField{
    private int msgTag;
    private int titleType;
    private String titleCont;
    private BasePostExtension extension;

    public int getMsgTag() {
        return msgTag;
    }

    public void setMsgTag(int msgTag) {
        this.msgTag = msgTag;
    }

    public int getTitleType() {
        return titleType;
    }

    public void setTitleType(int titleType) {
        this.titleType = titleType;
    }

    public String getTitleCont() {
        return titleCont;
    }

    public void setTitleCont(String titleCont) {
        this.titleCont = titleCont;
    }

    public BasePostExtension getExtension() {
        return extension;
    }

    public void setExtensions(BasePostExtension extension) {
        this.extension = extension;
    }
}
