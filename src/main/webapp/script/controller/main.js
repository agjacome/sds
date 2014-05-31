define([
    'angular', 'directive/index', 'filter/index', 'service/index',
], function(angular) {
    'use strict';

    return angular.module('smart-drug-search.controller', [
        'smart-drug-search.directive',
        'smart-drug-search.filter',
        'smart-drug-search.service'
    ]);

});
