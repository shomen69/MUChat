package com.shomen.MUChat.Singletons;

import android.util.Log;

import com.shomen.MUChat.Models.User;
import com.shomen.MUChat.Models.Conversation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by server on 1/25/2016.
 */
public class UsersListController {

    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();

    private static UsersListController uniqueInstance;

    HashMap<String,User> userListMap = new HashMap();
    private List<User> userList = new ArrayList<>();
    private List<HashMap<String,User>> userMapList = new ArrayList<>();

    private UsersListController(){};

    public static UsersListController getIntance(){
        if(uniqueInstance == null){
            uniqueInstance = new UsersListController();
        }
        return  uniqueInstance;
    }

    public List<User> getUserList() {
        return userList;
    }

    public HashMap<String,User> getUserListMap(){
        return userListMap;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void addUserToMap(User user){
        if(userListMap.containsKey(user.getId())){
            userListMap.get(user.getId()).setIsOnline(true);
            return;
        }
        Log.d(LOG_TAG, " size of map " + userListMap.size());
        userListMap.put(user.getId(), user);
        Log.d(LOG_TAG, " size of map " + userListMap.size());
        addUser(user);
    }

    public void addUser(User user){
        Log.d(LOG_TAG, " size of list " + userList.size());
        userList.add(user);
        Log.d(LOG_TAG, " size of list " + userList.size());
    }

    public void removeUser(String id){
        for(int i=0;i<userList.size();i++){
            if(userList.get(i).getId().equals(id)) userList.remove(i);
        }
        Log.d(LOG_TAG, "userList  size "+userList.size()+" removed id "+id);
    }

    public void removeUserFromMap(String id){
        if(userListMap.get(id).isChatting()){
            userListMap.get(id).setIsOnline(false);
            return;
        }
        Log.d(LOG_TAG, "userListMap size before removing "+userListMap.size());
        userListMap.remove(id);
        Log.d(LOG_TAG, "userListMap size after remove "+userListMap.size()+" removed id "+id);
        removeUser(id);
    }

    public void isUserExist(String id){
        if(userListMap.containsKey(id)){
            Log.d(LOG_TAG,"user still exist in map"+" id "+id);
        }else{
            Log.d(LOG_TAG,"user not exist in map id "+id);
        }
        User value = userListMap.get(id);
        if (value != null) {
            Log.d("ASL","user exist in map");
            for(int i=0;i<userList.size();i++){
                Log.d("ASL", "user  id "+userList.get(i).getId());
                if(userList.get(i).getId().equals(id)){
                    Log.d("ASL", "user  matched");
                }else{
                    Log.d("ASL", "user not matched");
                }
            }
        } else {
            Log.d("ASL","user don't exist in map");
        }
    }

    public void clearUserList(){
        userList.clear();
    }

    public void clearUserListMap(){
        userListMap.clear();
        clearUserList();
       }

    //////////////////////////////////////////////////////////////////////////////////

    public void updateUser(String id ,String msg ){

        Conversation conv = new Conversation(false,msg);
        User u = userListMap.get(id);
        u.addConversation(conv);

        if(!u.isChatting()){
            u.setNoti(u.getNoti() + 1);
        }

    }

}
