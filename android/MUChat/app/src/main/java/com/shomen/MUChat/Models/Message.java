package com.shomen.MUChat.Models;

/**
 * Created by server on 1/28/2016.
 */
public class Message {

    private String from ;
    private String msg ;

    public Message(String from, String msg) {
        this.from = from;
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
