define(['./main'], function(controller) {
    'use strict';

    var COUNT_PER_PAGE       = 10;
    var MAX_PAGINATION_LINKS =  5;

    var minimizeCompounds = function(results) {
        results.forEach(function(res) {
            res.keywords.forEach(function(keyword) {
                if (keyword.category === 'Compound')
                    keyword.normalized = keyword.normalized.split('/')[1];
            });
        });
    };

    var search = function(service, scope, rootScope) {
        service.search(scope.terms, scope.pageNumber, COUNT_PER_PAGE).then(
            function(response) {
                rootScope.error = false;
                scope.results   = response.data;
                minimizeCompounds(response.data.results);
            },
            function(error) {
                rootScope.error        = true;
                rootScope.errorMessage = error.data.err;
            }
        );
    };

    controller.controller('SearchResultsController', [
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
    ]);

});
