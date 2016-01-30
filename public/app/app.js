var app = angular.module('myApp', []);
app.controller('customersCtrl', function($scope, $http) {
/*    $http.get("http://www.w3schools.com/angular/customers.php")
    .then(function (response) {$scope.names = response.data.records;
    							console.log($scope.names);	
							  });

    $scope.users = [];
    $scope.name = "shomen";
    console.log("-----------"+$scope.users);*/

/*    $scope.myFunction = function() {      
	  console.log("inside myFunction clicked...");	  
	  socket.emit('join room',{ user_id:"123",user_name:"shomen",selected_room:"569233460d1e649c16608613" });
    }

    $scope.myFunctionOne = function(name,id,roomid) {
	  console.log("inside myFunctionOne clicked...");
	  socket.emit('join room',{ user_id:id,user_name:name,selected_room:roomid });
	}

    $scope.setUsers = function(users_list) {
	  console.log("");
	  $scope.users = users_list;
	}

    socket.on('into_room',function(data){
  		console.log("you are joined to room id "+JSON.stringify(data.users));   
  		console.log("-----------"+$scope.users.length);		
	});

	socket.on('for_everyone',function(data){
		$scope.name = "aaaaaaaaaaaa";
  		console.log(JSON.stringify(data));
	});*/
});