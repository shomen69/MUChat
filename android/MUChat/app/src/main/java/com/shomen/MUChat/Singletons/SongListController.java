package com.shomen.MUChat.Singletons;

import com.shomen.MUChat.Models.SongListDataModels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by server on 2/1/2016.
 */
public class SongListController {

    private final String LOG_TAG = "ASL_"+this.getClass().getSimpleName();

    private static SongListController uniqueInstance;

    private List<SongListDataModels> songsList = new ArrayList<>();

    private SongListController(){};

    public static SongListController getIntance(){
        if(uniqueInstance == null){
            uniqueInstance = new SongListController();
        }
        return  uniqueInstance;
    }

    public List<SongListDataModels> getSongsList() {
        return songsList;
    }

    public void setSongsList(List<SongListDataModels> songsList) {
        this.songsList = songsList;
    }

    public void addSongToList(SongListDataModels songModel){
        songsList.add(songModel);
    }

    public int getSongsListSize(){
        return songsList.size();
    }


}
