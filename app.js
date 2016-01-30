var express = require('express');
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var port = process.env.PORT || 3090;
var Room = require('./Room.js');
var User = require('./User.js');
const util = require('util');
var favicon = require('static-favicon');
var logger = require('morgan');
var bodyParser = require('body-parser');
var env = process.env.NODE_ENV = process.env.NODE_ENV || 'development';

var dbConfig = require('./server/config/dbConfig')[env];
require('./server/config/mongooseConfig')(dbConfig);

app.use(favicon());
// app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());
app.use(express.static(__dirname + '/public'))


var routes = require('./server/routes/authRoute')();
app.use('/', routes);


app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

var socketController = require('./server/controllers/socketController');
// socketController(io);

/*io.on('connection', function(socket){  

  console.log("a user connected");  

  socket.on('join_to_room', function(data){

    console.log("inside join room ----- user data"+JSON.stringify(data));

    var user_id = data.user_id.toString();
    users[user_id] = new User(data.user_name,data.user_id);
    sockets[user_id] = socket;
    
    console.log("inside join room------user socket id "+util.inspect(sockets[user_id].id));    
    
    var selected_room = data.selected_room.toString();    

    rooms[selected_room].addUser(users[user_id]);

    console.log("inside join room------selected room object "+JSON.stringify(rooms[selected_room]));

    var room_users_array = rooms[selected_room].getAllUsers();
    console.log("inside join room------users in room "+selected_room+" "+Object.keys(room_users_array).length); 
    
    socket.join(selected_room);

    io.to(selected_room).emit('new_user_joined', {users:Object.keys(room_users_array)});
    io.emit('for_everyone',{data:"a user connected"});

    console.log("--------------------------------------------------");
  });

  socket.on('private_chat',function(data){
    console.log('inside private chat------data :'+JSON.stringify(data));
    sockets[data.to].emit('send_private_msg',{from:data.from,msg:"hello"});
    io.to(data.room).emit('for_room', {data:"hello room users"});
    io.emit('for_all',{data:"hello all users"});
  });
});*/

/*http.listen(port, function () {
  console.log('Server listening at port %d', port);
});*/
app.listen(config.port);
console.log('Server listening at port %d', port);