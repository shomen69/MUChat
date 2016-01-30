function Room(name, id) {
  this.name = name;
  this.id = id;
  this.users = {}; 
};

Room.prototype.addUser = function(user_obj) {
  this.users[user_obj.id] = user_obj;
};

Room.prototype.removeUser = function(user_id) {
  console.log("inside Room object user id "+user_id);
  console.log("length of users before deletion :"+Object.keys(this.users).length+" "+JSON.stringify(Object.keys(this.users)));
  delete this.users[user_id];
  console.log("length of users after deletion :"+Object.keys(this.users).length);
  console.log("inside Room object user deleted");
};

Room.prototype.getAllUsers = function(){
  return this.users;
}

module.exports = Room;
