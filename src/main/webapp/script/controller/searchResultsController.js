define(['./main'], function(controller) {
    'use strict';

    var COUNT_PER_PAGE       = 10;
    var MAX_PAGINATION_LINKS =  5;

    var minimizeCompounds = function(results) {
        _.each(results, function(result) {
            _.each(result.keywords, function(keyword) {
                if (keyword.category === 'Compound')
                    keyword.normalized = keyword.normalized.split('/')[1];
            });
        });
    };

    var search = function(service, scope, rootScope) {
        scope.loading = true;
        service.search(scope.terms, scope.pageNumber, COUNT_PER_PAGE).then(
            function(response) {
                scope.loading   = false;
                rootScope.error = false;
                scope.results   = response.data;
                minimizeCompounds(response.data.results);
            },
            function(error) {
                scope.loading          = false;
                rootScope.error        = true;
                rootScope.errorMessage = error.data.err;
            }
        );
    };

    var searchResultsController = [
        '$scope', '$location', '$rootScope', '$routeParams', '$window', 'SearchService',
        function($scope, $location, $rootScope, $routeParams, $window, SearchService) {

            $scope.terms = $routeParams.terms || '';
            if (!$scope.terms.trim()) $location.path('/').search('terms', null)

            $scope.countPerPage  = COUNT_PER_PAGE;
            $scope.maxSize       = MAX_PAGINATION_LINKS;
            $scope.pageNumber    = 1;

            $rootScope.pageTitle = $scope.terms + $rootScope.pageTitle;

            $scope.pageChanged = function( ) {
                search(SearchService, $scope, $rootScope);
                $window.scrollTo(0, 0);
            };

            $scope.goToDocument = function(d) {
                $rootScope.terms = $routeParams.terms;
                $location.path('/document/' + d.id).search('terms', null);
            };

            search(SearchService, $scope, $rootScope);

        }
    ];

    controller.controller('SearchResultsController', searchResultsController);

});
