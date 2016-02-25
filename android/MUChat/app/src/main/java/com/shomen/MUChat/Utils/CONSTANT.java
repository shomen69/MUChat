package com.shomen.MUChat.Utils;

/**
 * Created by server on 1/20/2016.
 */
public class CONSTANT {
    //123.200.27.62
    public interface LOCAL_URL{
        public static final String BASE_URL = "http://192.168.0.251:3090";
        public static final String LOGIN_URL = BASE_URL+"/login";
        public static final String SIGNUP_URL = BASE_URL+"/signup";
        public static final String SONG_LIST_URL = BASE_URL+"/songs";
        public static final String SONG_THUMB_IMAGE = BASE_URL+"/imeges/";
        public static final String CHAT_SERVER_URL = BASE_URL;
    }

    public interface PRODUCTION_URL{
        public static final String BASE_URL = "http://192.168.0.251:3090";
        public static final String LOGIN_URL = BASE_URL+"/login";
        public static final String SIGNUP_URL = BASE_URL+"/signup";
        public static final String SONG_LIST_URL = BASE_URL+"/songs";
        public static final String SONG_THUMB_IMAGE = BASE_URL+"/imeges/";
        public static final String CHAT_SERVER_URL = BASE_URL;
    }

    public interface ACTION {
        public static String MAIN_ACTION = "main";
        public static String PREV_ACTION = "prev";
        public static String STOP_ACTION = "stop";
        public static String NEXT_ACTION = "next";
        public static String STARTFOREGROUND_ACTION = "startforeground";
        public static String STOPFOREGROUND_ACTION = "stopforeground";
        public static String DO_NOTHING = "donothing";
    }

    public interface NOTIFICATION_ID {
        public static int MUSIC_SERVICE_NOTI_ID = 101;
        public static int CHAT_SERVICE_NOTI_ID = 102;
    }


}
