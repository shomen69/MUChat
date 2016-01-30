function User(name, id) {
  this.name = name;
  this.id = id;
  // this.socket = socket; 
};

/*User.prototype.getSocket = function() {
  return this.socket;
};*/

/*Room.prototype.removeUser = function(user_obj) {
  var userIndex = -1;
  for(var i = 0; i < this.users.length; i++){
    if(this.users[i].id === user_obj.id){
      userIndex = i;
      break;
    }
  }
  this.users.remove(userIndex);
};

Room.prototype.getAllUsers = function(){
  return this.users;
}*/

module.exports = User;