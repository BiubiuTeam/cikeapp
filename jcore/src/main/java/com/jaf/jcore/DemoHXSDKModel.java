package com.jaf.jcore;

import android.content.Context;

/**
 * Created by vida2009 on 2015/5/19.
 */
public class DemoHXSDKModel extends DefaultHXSDKModel{
    public DemoHXSDKModel(Context ctx) {
        super(ctx);
    }

    // demo will not use HuanXin roster
    public boolean getUseHXRoster() {
        return false;
    }

    // demo will switch on debug mode
    public boolean isDebugMode(){
        return true;
    }

//    public boolean saveContactList(List<User> contactList) {
//        UserDao dao = new UserDao(context);
//        dao.saveContactList(contactList);
//        return true;
//    }

//    public Map<String, User> getContactList() {
//        UserDao dao = new UserDao(context);
//        return dao.getContactList();
//    }

//    public void closeDB() {
//        DbOpenHelper.getInstance(context).closeDB();
//    }

    @Override
    public String getAppProcessName() {
        return context.getPackageName();
    }
}
