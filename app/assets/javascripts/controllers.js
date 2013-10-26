var app = angular.module('app', ['ui.router']);

app.config(['$stateProvider', '$locationProvider', '$urlRouterProvider', '$httpProvider',
            function($stateProvider, $locationProvider, $urlRouterProvider, $httpProvider) {
    $locationProvider.html5Mode(true).hashPrefix('!');

	// --- Adds states.

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
    });

    $stateProvider.state({
    	name: 'checkLogin',
    	url: '/checkLogin',
    	templateUrl: '/assets/htmls/checkLogin.html',
    	controller: 'checkLoginController'
    });

	$stateProvider.state({
		name: 'loginRequired',
		url: '/loginRequired?redirectURL',
		templateUrl: '/assets/htmls/loginRequired.html'
	});

	$stateProvider.state({
		name: 'notFound',
		url: '/*all',
		templateUrl: '/assets/htmls/notfound.html'
	});

	// --- Adds global response interceptor.
	$httpProvider.interceptors.push(['$q', '$location', function($q, $location) { return {
	    'responseError': function(response) {
	    	// Checks response.status for global error handling.
			switch (response.status) {
			case 401: // UNAUTHORIZED
				var newURL = '/loginRequired?redirectURL=' + encodeURIComponent($location.url());
				$location.url(newURL);
				break;
			default:
				break;
			}

  			return $q.reject(response);
    	}
	};}]);
}]);

// ----------------------------------------------------------------------
// appController

app.controller('appController', ['$scope', '$http', function($scope, $http) {
	$scope.setLoginUser = function(user) {
		$scope.loginUser = user;
	}

	$scope.logout = function() {
		var args = {
			sessionToken: window.sessionToken
		};

		$http.post('/api/auth/logout', args).success(function(json) {
			location.href = '/';
		}).error(function(json) {
			console.log(json);
		});
	}

	if (window.loginUser) {
		$scope.setLoginUser(window.loginUser);
	}
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

        $http.post('/api/users/create', args).success(function(json) {
            location.href = '/';
        }).error(function(json) {
            console.log(json);
            alert('Failed');
        });
    }
}]);

// ----------------------------------------------------------------------
// Check Login Page

app.controller('checkLoginController', ['$scope', '$http', function($scope, $http) {
	$http.get('/api/demo/checkLogin').success(function(json) {
		$scope.login = 'OK';
	}).error(function(response) {
		console.log(response);
		$scope.login = 'NG';
	});
}]);

// ----------------------------------------------------------------------
// Error pages




// ----------------------------------------------------------------------
// Login

app.controller('loginFormController', ['$scope', '$http', function($scope, $http) {
	$scope.submit = function() {
		var args = {
			email: $scope.email,
			password: $scope.password,
			rememberMe: $scope.rememberMe,
			sessionToken: sessionToken
		};

		$http.post('/api/auth/login', args).success(function(json) {
			console.log(json);
			window.loginUser = json.user;
			window.sessionToken = json.sessionToken;
			$scope.setLoginUser(json.user);
		}).error(function(json) {
			alert('NG');
			console.log(json);
		});
	}
}]);

