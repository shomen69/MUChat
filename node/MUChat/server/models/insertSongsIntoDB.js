var async = require('async');
var SongModel = require('./SongModel');
var readDirectory = require('../readfile/readwritesongsdirectory');

function insertSongsIntoDB(songList) {
	console.log("inside insertSongsIntoDB size of songList "+songList.length);

	var fileNames = [];	
	SongModel.find({}).exec(function(err, collection) {

		if(collection.length === 0) {							  	
		  	async.each(songList,function(sample,next){
		  		var song = new SongModel();
		  		song.title = sample.toString();
		  		song.save(function(err,saved){		  			
		  			fileNames.push(saved);	  			
		  			next();
		  		});	 		
		  		
		  	},function(){
		  		console.log("all done------");
		  		readDirectory.renameDirectoryFiles(fileNames);
		  	})		  	
		  		  	
		}else{
			console.log("please drop this collection first");
		}
	})
}
exports.insertSongsIntoDB = insertSongsIntoDB;