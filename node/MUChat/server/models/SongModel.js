var mongoose = require('mongoose');
var async = require('async');

var songSchema = mongoose.Schema({
  title: {type:String, required:'true'}   
});

module.exports = mongoose.model('Songs',songSchema);