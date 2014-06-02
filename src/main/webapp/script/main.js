(function(requirejs) {
    'use strict';

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
        'ui-bootstrap-tpls',
        './app',
        './routes',
        './util'
    ], function(angular) {

        angular.bootstrap(document, ['smart-drug-search']);

    });

})(requirejs);
