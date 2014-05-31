define(['./main'], function(controller) {
    'use strict';

    var COUNT_PER_PAGE       = 10;
    var MAX_PAGINATION_LINKS =  5;

    var search = function(service, scope) {
        var s = service.search(scope.terms, scope.pageNumber, COUNT_PER_PAGE);
        s.success(function(data) {
            scope.success = true;
            scope.error   = false;
            scope.results = data;
            scope.results.results.forEach(function(res) {
                res.keywords.forEach(function(keyword) {
                    if (keyword.category === 'Compound')
                        keyword.normalized = keyword.normalized.split('/')[1];
                });
            });
        }).error(function(error) {
            scope.success      = false;
            scope.error        = true;
            scope.errorMessage = error.err;
        });
    };

    controller.controller('SearchResultsController', [
        '$scope', '$location', '$rootScope', '$routeParams', '$window', 'SearchService',
        function($scope, $location, $rootScope, $routeParams, $window, SearchService) {
            $scope.pageNumber    = 1;
            $scope.countPerPage  = COUNT_PER_PAGE;
            $scope.maxSize       = MAX_PAGINATION_LINKS;

            $scope.terms         = $routeParams.terms || '';
            $rootScope.pageTitle = $scope.terms + $rootScope.pageTitle;

            if (!$scope.terms.trim()) $location.path('/').search('terms', null);

            $scope.pageChanged = function( ) {
                search(SearchService, $scope);
                $window.scrollTo(0,0);
            };

            $scope.goToDocument = function(d) {
                $rootScope.terms = $routeParams.terms;
                $location.path('document/' + d.id).search('terms', null);
            };

            search(SearchService, $scope);
        }
    ]);

});
