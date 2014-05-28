(function(requirejs) {
    'use strict';

    requirejs.config({
        paths: {
            jsRoutes: '/api/jsroutes',
        },
        shim: {
            jsRoutes: { exports: 'jsRoutes' }
        },
        packages: [ 'controller', 'service', 'filter', 'directive' ],
    });

    requirejs.onError = function(err) {
        console.log(err);
    };

    require([
        'angular',
        'angular-cookies',
        'angular-resource',
        'angular-route',
        'jquery',
        'underscorejs',
        'bootstrap',
        'ui-bootstrap',
        './app',
        './routes'
    ], function(angular) {

        angular.bootstrap(document, ['smart-drug-search']);

    });

})(requirejs);
