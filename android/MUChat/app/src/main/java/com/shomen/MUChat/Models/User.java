package com.shomen.MUChat.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by server on 1/24/2016.
 */
public class User {

    private String id;
    private String name;
    private List<Conversation> conversationList = new ArrayList<>();
    private  int noti = 0;
    private boolean isChatting = false;

    private boolean isOnline = true;

    public User() {
    }

    public User(String id, String name) {
        this.id=id;
        this.name = name;
        this.isChatting = false;
        this.isOnline = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNoti() {
        return noti;
    }

    public void setNoti(int noti) {
        this.noti = noti;
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    public void addConversation(Conversation con){
        conversationList.add(con);
    };

    public boolean isChatting() {
        return isChatting;
    }

    public void setIsChatting(boolean isChatting) {
        this.isChatting = isChatting;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", conversationList=" + conversationList +
                ", noti=" + noti +
                ", isChatting=" + isChatting +
                ", isOnline=" + isOnline +
                '}';
    }

}
