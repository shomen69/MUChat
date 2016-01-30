var mongoose = require('mongoose');

var userSchema = mongoose.Schema({
  username: {type:String, required:'true'},
  password: {type:String, required:'true'}, 
  email: {type:String, required:'true'},
  firstName: {type:String, default: 'first'}, 
  lastName: {type:String, default: 'last'}
});

/*module.exports = mongoose.model('User',{	
	username: String,
	password: String,
	email: String,
	firstName: String,
	lastName: String
});*/

module.exports = mongoose.model('User',userSchema);