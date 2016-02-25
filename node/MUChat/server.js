const util = require('util');
var express = require('express');
var app = express();
var io = require('socket.io')(http);
var http = require('http').Server(app);

io.set('transports', ['xhr-polling']);
io.set('polling duration', 10);

var port = process.env.PORT || 3090;
app.use(express.static(__dirname + '/public'))

var users = {};
users["123456"] = {name:"shomen",id:"shoemn_123",socket:{id:"123",name:"s"}};
users["789054"] = {name:"shomen",id:"shoemn_123",socket:{id:"123",name:"s"}};
users.name  = "superman";
users.name = "batman";

console.log(util.inspect(users));
console.log("value of users "+users);
console.log("value of current date  "+new Date());

app.get('/', function(req, res){
  res.sendFile(__dirname + '/index.html');
});

http.listen(port, function () {
  console.log('Server listening at port %d', port);
});
