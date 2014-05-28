define(['app'], function(app) {
    'use strict';

    app.config(['$routeProvider', '$locationProvider',
        function($routeProvider, $locationProvider) {

            if (window.history && window.history.pushState)
                $locationProvider.html5Mode(true);

            $routeProvider.when('/', {
                pageTitle:   'SmartDrugSearch',
                templateUrl: '/assets/template/home.html'
            });

            $routeProvider.otherwise({ redirectTo: '/' });

        }
    ]);

})
