var mongoose = require('mongoose'),
  songsModel = require('../models/Songs');

module.exports = function(config) {
  mongoose.connect(config.db);
  var db = mongoose.connection;
  db.on('error', console.error.bind(console, 'connection error...'));
  db.once('open', function callback() {
    console.log('mySong db opened');
  });

  // songsModel.createDefaultSongs();

};

