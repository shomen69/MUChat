package com.shomen.MUChat.Models;

/**
 * Created by server on 1/25/2016.
 */
public class Conversation {

    private String message ;
    private boolean fromMe;

    public Conversation() {
    }

    public Conversation(boolean fromMe,String message) {
        this.fromMe = fromMe;
        this.message = message;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public void setFromMe(boolean fromMe) {
        this.fromMe = fromMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "message='" + message + '\'' +
                ", fromMe=" + fromMe +
                '}';
    }
}

