package com.shomen.MUChat.libs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by alchemy software on 9/20/2015.
 */
public class LoginSession {

    private Context context;
    private SharedPreferences sharedPreferences;
    private final String LOGIN_PREFS = "TOURS_LOGIN_PREF";
    private SharedPreferences.Editor editor;

    private final String IS_LOGIN = "is_login";
    private final String USER_EMAIL = "user_email";
    private final String USER_NAME = "user_name";
    private final String USER_ID = "user_id";
    private final String TOKEN  = "token";

    public LoginSession(Context context) {

        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String email,String name,String id,String token){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_NAME, name);
        editor.putString(USER_ID,id);
        editor.putString(TOKEN,token);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(USER_EMAIL, sharedPreferences.getString(USER_EMAIL, null));
        user.put(USER_NAME, sharedPreferences.getString(USER_NAME, null));
        user.put(USER_ID, sharedPreferences.getString(USER_ID, null));
        user.put(TOKEN, sharedPreferences.getString(TOKEN, null));

        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
    }

    public String getToken(){ return sharedPreferences.getString(TOKEN, null); }

    public String getUSER_ID(){return sharedPreferences.getString(USER_EMAIL,null);}

    public String getUSER_NAME(){return sharedPreferences.getString(USER_NAME,null);}

    public String getUSER_EMAIL() {
        return sharedPreferences.getString(USER_NAME,null);
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

}
