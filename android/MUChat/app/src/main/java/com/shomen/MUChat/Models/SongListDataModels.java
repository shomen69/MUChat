package com.shomen.MUChat.Models;

/**
 * Created by alchemy software on 1/16/2016.
 */
public class SongListDataModels {

    private String title;
    private String _id;

    public SongListDataModels() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "SongListDataModels{" +
                "title='" + title + '\'' +
                ", _id='" + _id + '\'' +
                '}';
    }
}
