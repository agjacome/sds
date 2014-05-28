define([
    'angular',
    'angular-cookies',
    'angular-resource',
    'angular-route',
    'controller/index',
    'directive/index',
    'filter/index',
    'service/index'
], function(angular) {

    var app = angular.module('smart-drug-search', [
        'smart-drug-search.controller',
        'smart-drug-search.directive',
        'smart-drug-search.filter',
        'smart-drug-search.service',
        'ngCookies',
        'ngRoute',
        'ngResource',
    ]);

    app.run(['$location', '$rootScope', function($location, $rootScope) {
        $rootScope.$on('$routeChangeSuccess', function(event, current, previous) {
            $rootScope.pageTitle = current.$$route.pageTitle || 'SmartDrugSearch';
        });
    }]);

    return app;

});
