var Songs = require('mongoose').model('Songs');
var Room = require('../../Room');

var rooms = [];

console.log("inside song controller");

exports.getSongs = function(done) {
    console.log("inside get songs...");
    var data ;
    Songs.find({}).exec(function(err, collection) {
        if(err){
            done(err,null);
        }else{
            data = collection;   
            done(null,data);
        }   
    })  
};

exports.getCourseById = function() {
    Songs.findOne({_id:""}).exec(function(err, song) {
        console.log(err+"  "+song);
    })
}