package com.yunkairichu.cike.bean;

import com.jaf.jcore.Application;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.main.ToolDevice;

import org.json.JSONObject;

/**
 * Created by vida2009 on 2015/5/7.
 */
public class JsonPack implements JsonConstant{

    public static JSONObject buildRegister(int userConfig, double latitude, double longitude, String city) {
        PostRegister pr = new PostRegister();
        pr.setCmd(CMD.USER_REG);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        pr.setDvcInfo(android.os.Build.MANUFACTURER);
        pr.setUserConfig(userConfig);
        pr.setLatitude(latitude);
        pr.setLongitude(longitude);
        pr.setCity(city);
        return JacksonWrapper.bean2Json(pr);
    }

    public static JSONObject buildUserInfoLoad() {
        PostUserInfoLoad pr = new PostUserInfoLoad();
        pr.setCmd(CMD.USER_INFO);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        return JacksonWrapper.bean2Json(pr);
    }

    public static JSONObject buildSearchTitle(int filter, int msgTag, double latitude, double longitude, String city) {

        PostSearchTitle pr = new PostSearchTitle();
        pr.setCmd(CMD.TITLE_SEARCH);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        pr.setLatitude(latitude);
        pr.setLongitude(longitude);
        pr.setCity(city);
        pr.setFilter(filter);
        pr.setMsgTag(msgTag);
        return JacksonWrapper.bean2Json(pr);
    }

    public static JSONObject buildPublishTitle(int msgTag, int titleType, String titleCont, int voiceTimeLen, int picSize, String Text, double latitude, double longitude, String city) {
        BasePostExtension postExtension = new BasePostExtension();
        postExtension.setPicSize(picSize);
        postExtension.setVoiceTimeLen(voiceTimeLen);
        postExtension.setText(Text);
        postExtension.setMsgTag(msgTag);

        PostPublishTitle pr = new PostPublishTitle();
        pr.setCmd(CMD.TITLE_PUBLISH);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        pr.setLatitude(latitude);
        pr.setLongitude(longitude);
        pr.setCity(city);
        pr.setMsgTag(msgTag);
        pr.setTitleType(titleType);
        pr.setTitleCont(titleCont);
        pr.setExtensions(postExtension);
        return JacksonWrapper.bean2Json(pr);
    }

    public static JSONObject buildUserChainInsert(String toDvcId, long titleId) {
        PostUserChainInsert pr = new PostUserChainInsert();
        pr.setCmd(CMD.CHAIN_INSERT);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        pr.setToDvcId(toDvcId);
        pr.setTitleId(titleId);
        return JacksonWrapper.bean2Json(pr);
    }

    public static JSONObject buildUserChainPull(int IdType, long lastId) {
        PostUserChainPull pr = new PostUserChainPull();
        pr.setCmd(CMD.CHAIN_QUERY);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        pr.setIdType(IdType);
        pr.setLastId(lastId);
        return JacksonWrapper.bean2Json(pr);
    }

    public static JSONObject buildPreTitlePull(int preTitleType) {
        PostPreTitlePull pr = new PostPreTitlePull();
        pr.setCmd(CMD.PRETITLE_PULL);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        pr.setPreTitleType(preTitleType);
        return JacksonWrapper.bean2Json(pr);
    }

    public static JSONObject buildSingleTitlePull(long titleId, double latitude, double longitude, String city) {
        PostSingleTitlePull pr = new PostSingleTitlePull();
        pr.setCmd(CMD.CHAIN_INSERT);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        pr.setLatitude(latitude);
        pr.setLongitude(longitude);
        pr.setCity(city);
        pr.setTitleId(titleId);
        return JacksonWrapper.bean2Json(pr);
    }

    public static JSONObject buildImpeach(int contType, long contId, int reason, String resonCont) {
        PostImpeach pr = new PostImpeach();
        pr.setCmd(CMD.IMPEACH);
        pr.setAppVersion(VER);
        pr.setPlatform(PLATFORM);
        pr.setDvcId(ToolDevice.getId(Application.getInstance().getApplicationContext()));
        pr.setContType(contType);
        pr.setContId(contId);
        pr.setReason(reason);
        pr.setReasonCont(resonCont);
        return JacksonWrapper.bean2Json(pr);
    }
}
