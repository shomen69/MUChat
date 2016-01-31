var socket = io.connect(window.location.hostname);

var user = {};

function myFunction() {
  console.log("inside myFunction clicked...");
  socket.emit('join room',{ user_id:"123",user_name:"shomen",selected_room:"569233460d1e649c16608613" });
}

function myFunctionOne(name,id,roomid) {
  user.id = id;
  user.name = name;
  user.roomid = roomid;
  console.log("inside myFunctionOne clicked...");
  socket.emit('join_to_room',{ user_id:id,user_name:name,selected_room:roomid });
}

function appendUsers(users_list){
  console.log('inside appenduser '+users_list.length);
  $("#table").empty();
  for(var i=0;i<users_list.length;i++){
    if(user.id === users_list[i]){
      $("#table").append("<tr><td><button type=\"button\" class=\"btn btn-primary disabled users\" id = "+users_list[i]+">"+users_list[i]+"</button></td></tr>");
    }else{
      $("#table").append("<tr><td><button type=\"button\" class=\"btn btn-primary users\" id = "+users_list[i]+">"+users_list[i]+"</button></td></tr>");
    }

  }
}

function appendSingleUser(user_id){
  console.log('inside appendSingleUser '+user_id); 
    if(user.id === user_id){
      $("#table").append("<tr><td><button type=\"button\" class=\"btn btn-primary disabled users\" id = "+user_id+">"+user_id+"</button></td></tr>");
    }else{
      $("#table").append("<tr><td><button type=\"button\" class=\"btn btn-primary users\" id = "+user_id+">"+user_id+"</button></td></tr>");
    }
}

function removeUser(user_id){
  console.log('inside removeUser '+user_id);
  $("#"+user_id).parent().parent().remove(); 
}

/*socket.on('new_user_joined',function(data){
  console.log("you are joined to room list of users "+JSON.stringify(data.id)); 
  appendUsers(data.id); 
});*/

socket.on('you_are_logged_in',function(data){
  console.log("you are logged in to a room  "+JSON.stringify(data.id)); 
  appendUsers(data.id); 
});

socket.on('user_joined',function(data){
  console.log("new user joined"+JSON.stringify(data.id)); 
  appendSingleUser(data.id); 
});

socket.on('send_private_msg',function(data){
  console.log(JSON.stringify(data));
});

socket.on('for_room',function(data){
  console.log(JSON.stringify(data));
});

socket.on('for_all',function(data){
  console.log(JSON.stringify(data));
});

socket.on('user_leaved',function(data){
  console.log("inside user_leaved "+JSON.stringify(data));
  removeUser(data.id);
});


function formSubmit(){
  var name = $("#username").val();
  var id = $("#userid").val();
  var roomid = $("#roomid").val();

  myFunctionOne(name,id,roomid);

  // alert(name +" "+ pass);
}

$(document).ready(function(){
  $("#myModal").modal({backdrop: "static"});

  $('#login').click(function(){
    formSubmit();
  });

  $('#my_button').click(function(){
    myFunction();
  });

  $('#table').on('click','button', function(){
    var to = $(this).attr('id').toString();
    console.log('you clicked '+$(this).attr('id'));
    socket.emit('private_chat',{from:user.id,to: to,room:user.roomid,msg:"hey there"});
  });

});
