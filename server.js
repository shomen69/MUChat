const util = require('util');

var users = {};
users["123456"] = {name:"shomen",id:"shoemn_123",socket:{id:"123",name:"s"}};
users["789054"] = {name:"shomen",id:"shoemn_123",socket:{id:"123",name:"s"}};
users.name  = "superman";
users.name = "batman";

console.log(util.inspect(users));
console.log("value of users "+users);
console.log("value of current date  "+new Date());
