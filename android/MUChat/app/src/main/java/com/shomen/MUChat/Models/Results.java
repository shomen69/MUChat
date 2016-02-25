package com.shomen.MUChat.Models;

import java.util.List;

/**
 * Created by server on 1/20/2016.
 */
public class Results {

    private String name;
    private String id;
    private String email;
    private String token;

    private List<SongListDataModels> songlist;

    public Results() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<SongListDataModels> getSonglist() {
        return songlist;
    }

    public void setSonglist(List<SongListDataModels> songlist) {
        this.songlist = songlist;
    }

    @Override
    public String toString() {
        return "Results{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", songlist=" + songlist +
                '}';
    }
}
