define(['./main'], function(controller) {
    'use strict';

    var documentShow = [
        '$scope', '$location', '$routeParams', '$rootScope', '$sce', 'DocumentService',
        function($scope, $location, $routeParams, $rootScope, $sce, DocumentService) {
            if (!($routeParams.id)) $location.path('/');

            $scope.goToSearch = function(annotation) {
                $location.path('/search').search('terms', annotation);
            };

            $scope.$watch('document', function( ) {
                if ($scope.success) {
                    $scope.document.trustedHtmlText = $sce.trustAsHtml(
                        $scope.document.text
                    );
                }
            });

            getDocument($routeParams.id, DocumentService, $scope, $rootScope);
        }
    ];

    var getDocument = function(id, service, scope, rootScope) {
        service.get({ id : id }, function(doc) {
            scope.success = true;
            scope.error   = false;

            doc.keywords.forEach(function(keyword) {
                if (keyword.category === 'Compound')
                    keyword.normalized = keyword.normalized.split('/')[1];
            });

            rootScope.pageTitle = doc.document.title + rootScope.pageTitle;
            setScopeDocument(scope, doc);
        }, function(error) {
            console.log(error);
            scope.success      = false;
            scope.error        = true;
            scope.errorMessage = error.data.err;

            rootScope.pageTitle = "ERROR" + rootScope.pageTitle;
        });
    };

    var setScopeDocument = function(scope, doc) {
        scope.document    = doc.document;
        scope.keywords    = doc.keywords;
        scope.annotations = doc.annotations;

        annotateDocumentText(
            scope.document, scope.keywords, scope.annotations
        );
    };

    var annotateDocumentText = function(doc, keywords, annotations) {
        filterDuplicateStartPositions(annotations).forEach(
            function(annotation) {
                doc.text = addAnnotation(
                    doc.text,
                    annotation.text,
                    annotation.startPosition,
                    annotation.endPosition,
                    findKeywordById(keywords, annotation.keywordId)
                );
            }
        );
    };

    var filterDuplicateStartPositions = function(annotations) {
        var noDuplicatePositions = [ ];
        var currStartPositions   = [ ];

        annotations.sort(function(x, y) {
            return y.startPosition - x.startPosition;
        }).forEach(function(annotation) {
            if (currStartPositions.indexOf(annotation.startPosition) === -1) {
                currStartPositions.push(annotation.startPosition);
                noDuplicatePositions.push(annotation);
            }
        });

        return noDuplicatePositions;
    };

    var addAnnotation = function(text, annotation, start, end, keyword) {
        var span  = '<span class="annotation ' + keyword.category.toLowerCase() +
                    '" data-popover="'         + keyword.normalized             +
                    '" data-popover-title="'   + keyword.category + '">'        +
                    annotation + '</span>';
        return text.splice(start, end - start, span);
    };

    var findKeywordById = function(keywords, id) {
        var index = keywords.map(function(k) { return k.id; }).indexOf(id);
        if (index !== -1) return keywords[index];
    };

    controller.controller('DocumentShowController', documentShow);

});
