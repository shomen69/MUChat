var express = require('express');
var router = express.Router();
var User = require('../models/UserModel');
var bCrypt = require('bcrypt-nodejs');
var jwt    = require('jsonwebtoken');
var songsController = require('../controllers/songsController');
var userController = require('../controllers/userController');


var isValidPassword = function(user, password){
    return bCrypt.compareSync(password, user.password);
}

function createJWT(usr){
	var token = jwt.sign({ foo: usr }, 'shhhhh', { expiresInMinutes: 60*10});
	return token;
}

function decodeJWT(_jwt){	
	return jwt.decode(_jwt);
}

module.exports = function(){

	router.post('/login', function(req,res){
		var _usr = req.body;
		console.log("body "+JSON.stringify(_usr));

		userController.getUserByEmail(_usr.email,function(err,user){
			if(err){
				console.log('Error in finding user '+err);
				 res.json({status:"error",message:"Internal database error",results:[]});
			}else{

				if(!user){
					console.log('User Not Found with this email '+_usr.email);
					res.json({status:"fail",message:"User Not Found with this email",results:[]});
				}else{
					console.log('User Found with this email '+_usr.email);
					if (!isValidPassword(user, _usr.password)){
	                    console.log('Invalid Password');
	                    res.json({status:"fail",message:"Invalid password ",results:[]});		                      
	                }else{
	                	console.log('Valid Password ');
	                	var jwt_user = {username:user.username, password:user.password}; 
	        			var token = createJWT(jwt_user);
	        			console.log("token created "+token);
	        			var data = [{token : token, name : user.username, id : user._id, email : user.email}]
		        		res.json({status:"success",message:"User login Successful ",results:data});
	                }
				}
			}
		});        
	});

	router.post('/signup',function(req,res){

		var _user = req.body;
		console.log("body :"+JSON.stringify(_user));

		userController.getUserByEmail(_user.email,function(err,user){
			if(err){
				console.log('Error in finding user '+err);
				res.json({status:"fail",message:"Internel DB error..",results:[]});
			}else{
				if(user){
					console.log('User Found with this email '+_user.email);
					res.json({status:"fail",message:"User already exists with this email ",results:[]});
				}else{
					console.log('User not Found with this email '+_user.email);
					userController.saveUserIntoDB(_user,function(err,savedData){
						if(err){
							console.log("Error in saving new user in DB "+err.toString());
							res.json({status:"fail",message:"Internel DB error..",results:[]});

						}else{
							console.log('user saved into DB '+JSON.stringify(savedData));
							var jwt_user = { username:savedData.username, password:savedData.password }; 
			        		var token = createJWT(jwt_user);
			        		var decodedJWT = decodeJWT(token);
			        		console.log("decoded token ......"+JSON.stringify(decodedJWT)); 
				        	console.log('token creation successful :'+token);
				        	var data = [{token : token, id : savedData._id, name : savedData.username, email : savedData.email }]
				        	res.json({status:"success",message:"User Creation Successful ",results:data});
						}
					})
				}
			}
		});
	});

	router.get('/songs/:token',function(req, res){

		console.log("inside /songs router....");

		jwt.verify(req.params.token, 'shhhhh', function(err, decoded) {
			console.log("inside jwt.verify: "+err, decoded);			
			if (err) {
				return res.json({ status:"fail", message: 'Failed to authenticate token.',results:[] });		
			} else {
				songsController.getSongs(function(err,data){
					if(err){
						return res.json({ status: "fail", message: 'Failed to authenticate token.',results:[] });
					}else{
						console.log("data : "+data);
						return res.json({ status: "success", message: 'success....',results:[{ songlist:data }] });
					}		
										
				});				
			}
		});	
	});

	/*------------Demo routes-------------*/
	router.post('/demo',function(req,res){
		console.log("inside /demo body "+JSON.stringify(req.body));
		var data = [{token : "12323435dsghd57"}]
		res.json({status:"success",message:"User login Successful ",results:data})
	});

	router.get('/demo',function(req,res){
		console.log("inside /demo body "+JSON.stringify(req.body));
		var data = [{token : "12323435dsghd57"}]
		res.json({status:"success",message:"User login Successful ",results:data})
	});

 	/*-------------------------------------*/

	/* GET Home Page */
	router.get('/home/:token',function(req, res){

		console.log("header..."+JSON.stringify(req.headers));

		jwt.verify(req.params.token, 'shhhhh', function(err, decoded) {
			console.log("inside verify: "+err, decoded);			
			if (err) {
				return res.json({ success: false, message: 'Failed to authenticate token.' });		
			} else {
				// if everything is good, save to request for use in other routes
				res.render( 'home', { data : {token : req.params.token, userData : decoded} } );
			}
		});	
	});

	/* Handle Logout */
	router.get('/signout', function(req, res) {
		req.logout();
		res.redirect('/');
	});

	return router;
}





