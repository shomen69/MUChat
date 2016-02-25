var path = require('path');
var rootPath = path.normalize(__dirname + '/../../');
console.log("rootpath..."+rootPath);

module.exports = {
  development: {
    db: 'mongodb://localhost/mySongChatApp',
    rootPath: rootPath
  },
  production: {
    rootPath: rootPath,
    db: 'mongodb://shomen:shomen69@ds051595.mongolab.com:51595/mysongchatapp'
  }
}