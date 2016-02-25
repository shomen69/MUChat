package com.shomen.MUChat.libs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by server on 2/2/2016.
 */
public class RoomsSession {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String ROOMS_PREF = "CHAT_ROOM_PREF";
    private SharedPreferences.Editor editor;

    public final static String IS_SET = "is_set";
    public final static String ROOM_ID = "room_id";
    public final static String ROOM_NAME = "room_name";

    public RoomsSession(Context context) {

        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(ROOMS_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createRoomSession(String id,String name){
        editor.putBoolean(IS_SET, true);
        editor.putString(ROOM_ID, id);
        editor.putString(ROOM_NAME, name);
        editor.commit();
    }

    public HashMap<String, String> getRoomDetails(){
        HashMap<String, String> room = new HashMap<>();
        room.put(ROOM_ID, sharedPreferences.getString(ROOM_ID, null));
        room.put(ROOM_NAME, sharedPreferences.getString(ROOM_NAME, null));

        return room;
    }

    public void clear(){
        editor.clear();
        editor.commit();
    }

    public boolean isSet(){
        return sharedPreferences.getBoolean(IS_SET, false);
    }

}
