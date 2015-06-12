package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class PostImpeach extends BasePostField{
    private int contType;
    private long contId;
    private int reason;
    private String toDvcId;
    private String reasonCont;

    public String getToDvcId() {
        return toDvcId;
    }

    public void setToDvcId(String toDvcId) {
        this.toDvcId = toDvcId;
    }

    public int getContType() {
        return contType;
    }

    public void setContType(int contType) {
        this.contType = contType;
    }

    public long getContId() {
        return contId;
    }

    public void setContId(long contId) {
        this.contId = contId;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public String getReasonCont() {
        return reasonCont;
    }

    public void setReasonCont(String reasonCont) {
        this.reasonCont = reasonCont;
    }
  }
