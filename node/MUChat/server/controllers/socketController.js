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
				console.log("all rooms "+JSON.stringify(Object.keys(rooms)));
			});


function userJoinedResponseBuilder(users_in_room){
	console.log("inside userJoinedResponseBuilder method...");
	var id = Object.keys(users_in_room);
    var name =[];
	for (var i = 0; i < id.length; i++) {
	    name.push(users_in_room[id[i]].name);			   
	} 
	console.log("  ----- "+JSON.stringify(name)+" -- "+JSON.stringify(id)); 

	return {id:id,name:name};
}

function userLeftRoom(user_id,room_id,socket){

	console.log("inside userLeftRoom method "+user_id+" "+room_id);

	if(user_id != null){				

		if (users.hasOwnProperty(user_id)){
			socket.broadcast.to(room_id).emit('user_leaved',{id:user_id});
			socket.leave(room_id);

			console.log("length of users before deletion :"+Object.keys(users).length); 
			delete users[user_id];
			console.log("length of users after deletion :"+Object.keys(users).length);
			
		}else{
			console.log("theres is no "+user_id+" property in users object ");
		}				
		
	}else{
		console.log("user_id is not defined or null..");
	}

	if(room_id != null){		
		rooms[room_id].removeUser(user_id);
	}else{
		console.log(" room_id is not defined or null..");
	}
}

module.exports = function(io){	

	io.on('connection', function(socket){

		console.log("a user connected "+socket.id); 

		var this_user_selected_room;
		var this_user_id;
		var this_socket = socket.id;	  	 

	  	socket.on('join_to_room', function(data){

		    console.log("inside join room method----- user data "+JSON.stringify(data));

		    this_user_id = data.user_id.toString();
		    this_user_selected_room = data.selected_room.toString();

		    /// checking for previous bad disconnection---------///////
		    userLeftRoom(this_user_id,this_user_selected_room,socket);
		    
		    users[this_user_id] = new User(data.user_name,data.user_id);
		    sockets[this_user_id] = socket;
		   
		    rooms[this_user_selected_room].addUser(users[this_user_id]);

		    console.log("inside join room------selected room object "+JSON.stringify(rooms[this_user_selected_room]));
					    
		    socket.join(this_user_selected_room);

		    socket.emit('you_are_logged_in',userJoinedResponseBuilder(rooms[this_user_selected_room].getAllUsers()))
		    socket.broadcast.to(this_user_selected_room).emit('user_joined',{id:this_user_id,name:data.user_name});
		    // io.to(this_user_selected_room).emit('new_user_joined', {id:id,name:name});
		    io.emit('for_everyone',{data:"a user connected"});

		    console.log("--------------------------------------------------");
	  	});

		socket.on('demo_event',function(data){
			console.log('inside demo event....');
			var user_id = data.from.toString();
			sockets[user_id] = socket
			sockets[data.to].emit('demo_event',"shomen");
		});

		socket.on('leave_from_room',function(data){
			console.log('inside leave_from_room------data :'+JSON.stringify(data));
			userLeftRoom(this_user_id,this_user_selected_room,socket);
		})

		socket.on('private_chat',function(data){
			console.log('inside private chat------data :'+JSON.stringify(data));
			if(this_user_selected_room != null && rooms[this_user_selected_room].getAllUsers().hasOwnProperty(data.to))
				sockets[data.to].emit('send_private_msg',{from:data.from,msg:data.msg});
			io.to(data.room).emit('for_room', {data:"hello room users"});
			io.emit('for_all',{data:"hello all users"});
		});

		socket.on('typing',function(data){
			console.log('inside typing------data :'+JSON.stringify(data));
			if(this_user_selected_room != null && rooms[this_user_selected_room].getAllUsers().hasOwnProperty(data.to))
				sockets[data.to].emit('typing',{from:data.from});
		})

		socket.on('stop_typing',function(data){
			console.log('inside stop_typing------data :'+JSON.stringify(data));
			if(this_user_selected_room != null && rooms[this_user_selected_room].getAllUsers().hasOwnProperty(data.to))
				sockets[data.to].emit('stop_typing',{from:data.from});
		})

		socket.on('disconnect', function () {
			console.log("---------------------a user disconnected---------------------------");
			
			if(!this_user_id)
				return;		
			
			if( this_socket === sockets[this_user_id].id ){
				console.log("this_socket id "+util.inspect(this_socket)+" user "+util.inspect(sockets[this_user_id].id));	
				userLeftRoom(this_user_id,this_user_selected_room,socket);
			}else{
				console.log("sockets ids are not same... ");
			}
			console.log("-------------------------------------------------------------------");
		});
	});

}