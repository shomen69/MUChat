var async = require('async');
var fs = require("fs");
var path = require('path');
var fPath = path.normalize(__dirname + '/../../public/songs/');

function readDirectoryFiles(done){
	console.log("inside readDirectoryFiles function ");

	fs.readdir(fPath, function (err, files) {	
		if(err){
			console.log("error "+err);
			done(err,null);			
		}else{
			for (var i = files.length - 1; i >= 0; i--) {
				var index = files[i].toString().search(".mp3");
				files[i] = files[i].toString().substr(0,index); 				
			};	
			// console.log(JSON.stringify(files));	
			done(null,files);			
			// insertSong.insertSongsIntoDB(files)			
		}	
	});
}

function renameDirectoryFiles(files,done) {
	console.log("inside renameDirectoryFiles function size of files "+files.length);
	async.each(files,function(song,next){
		// console.log(fPath+song.title+".mp3");					
		fs.rename(fPath+song.title+".mp3",fPath+song._id+".mp3",function(err){
			if(err)
				next(new Error("can't rename files..."));
			else
				next()	;
		});		 			

	},function(err,result){		
		if(err){
			done(err,null);
		}else{
			done(null,true);
		}				  		
	})	
}

exports.readDirectoryFiles = readDirectoryFiles;
exports.renameDirectoryFiles = renameDirectoryFiles;
