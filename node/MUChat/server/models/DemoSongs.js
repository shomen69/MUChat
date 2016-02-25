var mongoose = require('mongoose');
var async = require('async');
var rename = require('../readfile/readingsongsfile');

var demoSongSchema = mongoose.Schema({
  title: {type:String, required:'true'}   
});

var DemoSongs = mongoose.model('DemoSongs',demoSongSchema);

function createDefaultDemoSongs(songList) {
	console.log("inside createDefaultDemoSongs size of songList "+songList.length);
	
	DemoSongs.find({}).exec(function(err, collection) {

		if(collection.length !== 0) {
			var fileName = [];				  	
		  	async.each(songList,function(sample,next){
		  		var song = new DemoSongs();
		  		song.title = sample.toString();
		  		song.save(function(err,saved){
		  			console.log(saved._id.toString());	
		  			fileName.push(saved);	  			
		  			next();
		  		});		 		
		  		
		  	},function(){
		  		console.log("all done--"+fileName);	
		  		rename.renameFile(fileName);
		  	})
		  	
		  		  	
		}else{
			console.log("not empty");
		}
	})
}

exports.createDefaultDemoSongs = createDefaultDemoSongs;