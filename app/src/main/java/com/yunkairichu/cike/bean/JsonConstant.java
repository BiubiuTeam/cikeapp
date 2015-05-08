package com.yunkairichu.cike.bean;

/**
 * Created by vida2009 on 2015/5/7.
 */
public interface JsonConstant {
    public static final String KEY_PUSH_EXTRA = "push_detail_extra";

    public static final String KEY_PUSH_DETAIL = "PUSH_START_DETAIL";

    public static final String CGI = "http://183.131.76.109/cgi_wl/user_svc.php";
    public static final String VER = "2.1";
    public static final String PLATFORM = "Android";
    public static final String MULTIPART = "http://183.131.76.109/wuliao_upload.php";

    public interface CMD {
        public static final int USER_REG = 0x1001;
        public static final int USER_INFO = 0x1002;

        public static final int TITLE_SEARCH = 0x2001;
        public static final int TITLE_PUBLISH = 0x2002;
        public static final int CHAIN_INSERT = 0x2003;
        public static final int CHAIN_QUERY = 0x2004;
        public static final int PRETITLE_PULL = 0x2005;
        public static final int ONE_TITLE_PUSH = 0x2006;

        public static final int IMPEACH = 0x5001;
    }
}
