var env = process.env.NODE_ENV = process.env.NODE_ENV || 'development';
var dbConfig = require('../config/dbConfig')[env];
require('../config/mongooseConfig')(dbConfig);
var async = require('async');
var rwDirectory = require('./readwritesongsdirectory');
var songController = require('../controllers/songsController');

var listOfFiles = [];

async.series([
		function(callback){
			rwDirectory.readDirectoryFiles(function(err,data){
				if(err){
					callback(new Error("Problem in task 1 "+err.toString(),null));		
				}else{
					listOfFiles = data;
					callback(null,true);	
				}	
			});
		},
		function(callback){
			songController.insertSongsIntoDB(listOfFiles,function(err,data){
				if(err){
					callback(new Error("Problem in task 2 "+err.toString(),null));
				}else{
					listOfFiles = data;
					callback(null,true);
				}
			})
		},
		function(callback){
			rwDirectory.renameDirectoryFiles(listOfFiles,function(err,data){
				if(err){
					callback(new Error("Problem in task 3 "+err.toString(),null));		
				}else{
					listOfFiles = data;
					callback(null,true);	
				}	
			});
		}
	],function(err, results){
		if(err){
			console.log("inside main.js async.series final method "+err.toString());
		}else{
			console.log("inside main.js async.series final method "+results.toString());
		}
});