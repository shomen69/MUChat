var mongoose = require('mongoose');

var songSchema = mongoose.Schema({
  title: {type:String, required:'true'}   
});
var Songs = mongoose.model('Songs', songSchema);

function createDefaultSongs() {
  Songs.find({}).exec(function(err, collection) {
    if(collection.length === 0) {
      Songs.create({ title: 'song name'});
      Songs.create({ title: 'song name'});   
    }
  })
}

exports.createDefaultSongs = createDefaultSongs;