define([
    'angular', 'directive/main', 'filter/main', 'service/main'
], function(angular) {
    'use strict';

    return angular.module('smart-drug-search.controller', [
        'smart-drug-search.directive',
        'smart-drug-search.filter',
        'smart-drug-search.service'
    ]);

});
