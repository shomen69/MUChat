var Room = require('../../Room.js');
var User = require('../../User.js');
const util = require('util');

var songsController = require('./songsController');

console.log("inside socket controller");

var users = {};

var rooms = {};

var sockets = {};

var data = 	songsController.getSongs(function(err,data){
				console.log("inside callback "+data);
				for(var i =0 ;i<data.length;i++){
				var id = data[i]._id.toString();
				rooms[id] = new Room(data[i].title , data[i]._id);           
				}
				// console.log("inside callback "+JSON.stringify(rooms));	
				console.log("all rooms "+JSON.stringify(Object.keys(rooms)));
			});


function buildResponseForUserJoined(users_in_room){

	var id = Object.keys(users_in_room);
    var name =[];
	for (var i = 0; i < id.length; i++) {
	    name.push(users_in_room[id[i]].name);			   
	} 
}

module.exports = function(io){	

	io.on('connection', function(socket){

		var this_user_id ;  
		var this_user_selected_room;

	  	console.log("a user connected");  

	  	socket.on('join_to_room', function(data){

		    console.log("inside join room ----- user data "+JSON.stringify(data));

		    var user_id = data.user_id.toString();
		    users[user_id] = new User(data.user_name,data.user_id);
		    sockets[user_id] = socket;
		    
		    console.log("inside join room------user socket id "+util.inspect(sockets[user_id].id));    
		    
		    var selected_room = data.selected_room.toString(); 


		    this_user_id = user_id;
		    this_user_selected_room = selected_room;   

		    rooms[selected_room].addUser(users[user_id]);

		    console.log("inside join room------selected room object "+JSON.stringify(rooms[selected_room]));

		    var room_users_array = rooms[selected_room].getAllUsers();		   
		     
		    var id = Object.keys(room_users_array);
		    var name =[];
			for (var i = 0; i < id.length; i++) {
			    name.push(room_users_array[id[i]].name);			   
			} 

			 console.log("  ----- "+JSON.stringify(name)+" -- "+JSON.stringify(id)); 

		    
		    socket.join(selected_room);

		    socket.emit('you_are_logged_in',{id:id,name:name})
		    socket.broadcast.to(selected_room).emit('user_joined',{id:user_id,name:data.user_name});
		    // io.to(selected_room).emit('new_user_joined', {id:id,name:name});
		    io.emit('for_everyone',{data:"a user connected"});

		    console.log("--------------------------------------------------");
	  	});

		socket.on('private_chat',function(data){
			console.log('inside private chat------data :'+JSON.stringify(data));
			sockets[data.to].emit('send_private_msg',{from:data.from,msg:data.msg});
			io.to(data.room).emit('for_room', {data:"hello room users"});
			io.emit('for_all',{data:"hello all users"});
		});

		socket.on('typing',function(data){
			console.log('inside typing------data :'+JSON.stringify(data));
			sockets[data.from].emit('typing',{from:data.from});
		})

		socket.on('stop_typing',function(data){
			console.log('inside stop_typing------data :'+JSON.stringify(data));
			sockets[data.from].emit('stop_typing',{from:data.from});
		})

		socket.on('disconnect', function () {
			console.log("------------------------------------------------");
			console.log("a user left");

			if(this_user_id != null){
				console.log("this_user_id is not null..");
				socket.broadcast.to(this_user_selected_room).emit('user_leaved',{id:this_user_id});
				if (users.hasOwnProperty(this_user_id)){
					console.log("property is here...");

					console.log("length of users before deletion :"+Object.keys(users).length); 
					delete users[this_user_id];
					console.log("length of users after deletion :"+Object.keys(users).length);


					if(users.hasOwnProperty(this_user_id)){
						console.log("property not deleated...");	
					}else{
						console.log("property  deleated...");
					}
				}				
				
			}else{
				console.log("this_user_id is  null..");
			}

			if(this_user_selected_room != null){
				console.log("this_user_selected_room is not null..");
				rooms[this_user_selected_room].removeUser(this_user_id);
			}else{
				console.log("this_user_selected_room is  null..");
			}

			console.log("------------------------------------------------");
		});
	});

}