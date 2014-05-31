define(['./main'], function(controller) {
    'use strict';

    controller.controller('SearchController', [
        '$scope', '$location', '$rootScope', '$routeParams',
        function($scope, $location, $rootScope, $routeParams) {
            $scope.terms = $routeParams.terms || $rootScope.terms || '';

            $scope.search = function() {
                if ($scope.terms !== '')
                    $location.path('/search').search('terms', $scope.terms);
            }
        }
    ]);

});
