var SongModel = require('../models/SongModel');
var async = require('async');

var rooms = [];

console.log("inside song controller");

exports.getSongs = function(done) {
    console.log("inside get songs...");
    var data ;
    SongModel.find({}).exec(function(err, collection) {
        if(err){
            done(err,null);
        }else{
            data = collection;   
            done(null,data);
        }   
    })  
};

exports.getSongById = function() {
    SongModel.findOne({_id:""}).exec(function(err, song) {
        console.log(err+"  "+song);
    })
}

exports.insertSongsIntoDB = function (songList,done) {
    console.log("inside insertSongsIntoDB size of songList "+songList.length);

    var fileNames = []; 

    SongModel.find({}).exec(function(err, collection) {
        if(err){
            done(new Error("can't find songmodel data from DB"),null);
        }else{
            if(collection.length === 0) {
                var i = 0;                              
                async.each(songList,function(sample,next){
                    var song = new SongModel();
                    song.title = sample.toString();                                 
                    
                    song.save(function(err,saved){                  
                        fileNames.push(saved);
                        if(err)         
                            next(new Error("can't save song data in DB"));
                        else
                            next();         
                        });         
                    
                },function(err,result){
                    if(err){                        
                        done(err,null);
                    }else{                      
                        done(null,fileNames);                   
                    }
                    
                });     
            }else{
                done(new Error("please drop this collection first"),null);              
            }
        }    
    })
}