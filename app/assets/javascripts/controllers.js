var app = angular.module('app', ['ui.router']);

app.config(['$stateProvider', '$routeProvider', '$locationProvider',
            function($stateProvider, $routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true).hashPrefix('!');

    $stateProvider.state({
        name: 'top',
        templateUrl: '/assets/htmls/top.html'
    });

    $stateProvider.state({
        name: 'home',
        url: '/',
        parent: 'top',
        templateUrl: '/assets/htmls/index.html',
        controller: 'indexController'
    });

    $stateProvider.state({
        name: 'userCreate',
        url: '/users/create',
        templateUrl: '/assets/htmls/users/create.html',
        controller: 'userCreateController'
    })
}]);

// ----------------------------------------------------------------------
// appController

app.controller('appController', ['$scope', '$http', function($scope, $http) {
}]);

// ----------------------------------------------------------------------
// IndexController

app.controller('indexController', ['$scope', '$http', function($scope, $http) {
}]);

// ----------------------------------------------------------------------
// UserCreateController

app.controller('userCreateController', ['$scope', '$http', function($scope, $http) {
    $scope.submit = function() {
        if ($scope.password != $scope.confirmation) {
            alert('パスワードが一致していません。');
            return;
        }

        var args = {
            email: $scope.email,
            loginId: $scope.loginId,
            nickname: $scope.nickname,
            password: $scope.password,
            sessionToken: window.sessionToken
        };

        $http.post('/api/users/create', args).success(function (json) {
            location.href = '/';
        }).error(function (json) {
            console.log(json);
            alert('Failed');
        });
    }
}]);


