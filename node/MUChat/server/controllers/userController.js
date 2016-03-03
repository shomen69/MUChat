var bCrypt = require('bcrypt-nodejs');
var UserModel = require('../models/UserModel');

console.log("inside user controller");

// Generates hash using bCrypt
var createHash = function(password){
    return bCrypt.hashSync(password, bCrypt.genSaltSync(10), null);
}

exports.getUserByEmail = function(email,done) {
    console.log("inside getUserByEmail...");
    UserModel.findOne({ 'email' : email },function(err,user){
    	if(err){    		 
    		done(err,null);  
    	}else{
    		done(null,user);
    	}
    }) 
};

exports.saveUserIntoDB = function(user,done){
	console.log("inside saveUserIntoDB...");

    var newUser = new UserModel();
    // set the user's local credentials
    newUser.username = user.username;
    newUser.password = createHash(user.password);
    newUser.email = user.email;
    newUser.firstName = user.firstName
    newUser.lastName = user.lastName;
    
    newUser.save(function(err,savedData) {
        if (err){        	
        	done(err,null);		                
        }else{        	
        	done(null,savedData); 
        }	                  		            
	});
}

