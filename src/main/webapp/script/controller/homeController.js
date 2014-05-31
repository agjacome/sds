define(['./main'], function(controller) {
    'use strict';

    controller.controller('HomeController', [
        '$rootScope', function($rootScope) {
            $rootScope.terms = null;
        }
    ]);

});
