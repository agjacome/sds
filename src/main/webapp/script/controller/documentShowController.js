define(['./main'], function(controller) {
    'use strict';

    var success = function(data, scope, sce) {
        scope.document    = data.document;
        scope.keywords    = data.keywords;
        scope.annotations = data.annotations;

        minimizeCompounds(scope.keywords);
        annotateDocumentText(scope.document, scope.keywords, scope.annotations);

        scope.document.text = sce.trustAsHtml(scope.document.text);
    };

    var minimizeCompounds = function(keywords) {
        keywords.forEach(function(keyword) {
            if (keyword.category === 'Compound')
                keyword.normalized = keyword.normalized.split('/')[1];
        });
    };

    var annotateDocumentText = function(doc, keywords, annotations) {
        filterDuplicateStartPositions(annotations).forEach(
            function(annotation) {
                var keyword = findById(keywords, annotation.keywordId);
                doc.text    = addAnnotation(doc.text, annotation, keyword);
            }
        );
    };

    var filterDuplicateStartPositions = function(annotations) {
        var cmp = function(x, y) { return y.startPosition - x.startPosition };

        var noDuplicated   = [ ];
        var startPositions = [ ];

        annotations.sort(cmp).forEach(function(annotation) {
            if (startPositions.indexOf(annotation.startPosition) === -1) {
                startPositions.push(annotation.startPosition);
                noDuplicated.push(annotation);
            }
        });

        return noDuplicated;
    };

    var addAnnotation = function(text, annotation, keyword) {
        var span =
            '<span class="annotation ' + keyword.category.toLowerCase() +
            '" data-popover="'         + keyword.normalized             +
            '" data-popover-title="'   + keyword.category + '">'        +
            annotation.text + '</span>';

        return text.splice(
            annotation.startPosition,
            annotation.endPosition - annotation.startPosition,
            span
        );
    };

    var findById = function(array, id) {
        var index = array.map(function(k) { return k.id; }).indexOf(id);
        if (index !== -1) return array[index];
    };


    controller.controller('DocumentShowController', [
        '$scope', '$location', '$routeParams', '$rootScope', '$sce', 'DocumentService',
        function($scope, $location, $routeParams, $rootScope, $sce, DocumentService) {

            if (!($routeParams.id)) $location.path('/');

            $scope.goToSearch = function(annotation) {
                $location.path('/search').search('terms', annotation);
            };

            DocumentService.get({ id : $routeParams.id }).$promise.then(
                function(data) {
                    $rootScope.error     = false;
                    $rootScope.pageTitle = data.document.title + $rootScope.pageTitle;
                    success(data, $scope, $sce);
                },
                function(response) {
                    $rootScope.error        = true;
                    $rootScope.errorMessage = response.data.err;
                    $rootScope.pageTitle    = 'Error ' + $rootScope.pageTitle;
                }
            );

        }
    ]);

});
