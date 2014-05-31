define(['app'], function(app) {
    'use strict';

    app.config([
        '$routeProvider', '$locationProvider',
        function($routeProvider, $locationProvider) {

            if (window.history && window.history.pushState)
                $locationProvider.html5Mode(true).hashPrefix('!');

            $routeProvider.when('/', {
                pageTitle   : 'SmartDrugSearch',
                templateUrl : '/assets/template/home.html',
                controller  : 'HomeController',
            });

            $routeProvider.when('/search', {
                pageTitle   : ' :: SmartDrugSearch',
                templateUrl : '/assets/template/searchResults.html',
                controller  : 'SearchResultsController',
            });

            $routeProvider.when('/document/:id', {
                pageTitle      : ' :: Document :: SmartDrugSearch',
                templateUrl    : '/assets/template/documentShow.html',
                controller     : 'DocumentShowController',
                reloadOnSearch : false,
            });

            $routeProvider.otherwise({ redirectTo : '/' });

        }
    ]);

})
