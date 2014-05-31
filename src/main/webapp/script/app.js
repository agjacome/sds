define([
    'angular',
    'angular-cookies',
    'angular-resource',
    'angular-route',
    'ui-bootstrap',
    'ui-bootstrap-tpls',
    './controller/index',
    './directive/index',
    './filter/index',
    './service/index'
], function(angular) {

    var app = angular.module('smart-drug-search', [
        'ngCookies', 'ngRoute', 'ngResource',
        'ui.bootstrap', 'ui.bootstrap.tpls',
        'smart-drug-search.controller',
        'smart-drug-search.directive',
        'smart-drug-search.filter',
        'smart-drug-search.service',
    ]);

    app.run(['$location', '$rootScope', function($location, $rootScope) {
        $rootScope.$on('$routeChangeSuccess', function(event, current, previous) {
            $rootScope.pageTitle = current.$$route.pageTitle || 'SmartDrugSearch';
        });
    }]);

    return app;

});
