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
socketController(io);

http.listen(port, function () {
  console.log('Server listening at port %d', port);
});
