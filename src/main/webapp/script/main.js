(function(requirejs) {
    'use strict';

    requirejs.onError = function(err) {
        console.log(err);
    };

    require([
        'angular',
        'jquery',
        'underscorejs',
        'bootstrap',
        'angular',
        './app',
        './routes',
        './util'
    ], function(angular) {

        angular.bootstrap(document, ['smart-drug-search']);

    });

})(requirejs);
