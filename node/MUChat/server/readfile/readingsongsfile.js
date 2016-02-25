var dbConfig = require('../config/dbConfig')['development'];
require('../config/mongooseConfig')(dbConfig);
var async = require('async');
var fs = require("fs");
var demoSongModel = require('../models/DemoSongs');

var songsList;

fs.readdir('../../public/songs', function (err, files) {
	
	if(err){
		console.log("error "+err);
	}else{
		for (var i = files.length - 1; i >= 0; i--) {
			var s = files[i].toString().search(".mp3");
			files[i] = files[i].toString().substr(0,s); 				
		};
		songsList = files;		
		console.log(JSON.stringify(songsList));	
		demoSongModel.createDefaultDemoSongs(songsList)
	}	
});


function renameFile(songList) {
	console.log("inside renameFile size of songList "+songList.length);
	async.each(songList,function(song,next){
		console.log("  "+song.title+".mp3");
		// fs.rename(song.title+".mp3",song._id+".mp3",function(err){
		// 	if(err){
		// 		console.log(err);
		// 		return;
		// 	}
		// })	 			

	},function(){
		console.log("all done--"+fileName);		  		
	})	
	
}

exports.renameFile = renameFile;

console.log("reading files...");



