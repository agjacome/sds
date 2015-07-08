define(['./main'], function(controller) {
    'use strict';

    var COLORS = {
        'Compound' : '#59ABE3',
        'Gene'     : '#BF55EC',
        'Protein'  : '#F39C12',
        'Species'  : '#2ECC71',
        'DNA'      : '#E74C3C',
        'RNA'      : '#95A5A6',
        'CellLine' : '#96281B',
        'CellType' : '#34495E',
    };

    var COUNT_PER_PAGE       = 10;
    var MAX_PAGINATION_LINKS =  5;

    sigma.classes.graph.addMethod('neighbors', function(nodeId) {
        var k,
        neighbors = {},
        index = this.allNeighborsIndex[nodeId] || {};

        for (k in index)
            neighbors[k] = this.nodesIndex[k];

        return neighbors;
    });

    var minimizeCompounds = function(results) {
        _.each(results, function(result) {
            _.each(result.keywords, function(keyword) {
                if (keyword.category === 'Compound')
                    keyword.normalized = keyword.normalized.split('/')[1];
            });
        });
    };

    var getGraph = function(results) {
        var graph = { nodes: [ ], edges: [ ] };

        var keywords = _.flatten(_.map(results, function(result) {
            return result.keywords;
        }));

        var nodeIds = [ ];
        _.each(keywords, function(keyword) {
            var node = {
                id            : '' + keyword.id,
                x             : Math.random(),
                y             : Math.random(),
                size          : 10,
                label         : keyword.normalized,
                originalLabel : keyword.normalized,
                color         : COLORS[keyword.category],
                originalColor : COLORS[keyword.category]
            };

            if (!_.contains(nodeIds, node.id)) {
                graph.nodes.push(node)
                nodeIds.push(node.id);
            }
        });

        var edgeIds = [ ];
        _.each(results, function(doc) {
            _.each(doc.keywords, function(k1) {
                _.each(doc.keywords, function(k2) {
                    var edge = {
                        id            : '' + k1.id + '-' + k2.id,
                        source        : '' + k1.id,
                        target        : '' + k2.id,
                        size          : 1,
                        color         : '#444444',
                        originalColor : '#444444',
                    };

                    if (!_.contains(edgeIds, edge.id)) {
                        graph.edges.push(edge);
                        edgeIds.push(edge.id);
                        edgeIds.push('' + k2.id + '-' + k1.id);
                    }
                });
            });
        });


        return graph;
    };

    var bindClickToSigma = function(s) {
        s.bind('clickNode', function(e) {
            var nodeId = e.data.node.id,
            toKeep = s.graph.neighbors(nodeId);
            toKeep[nodeId] = e.data.node;

            s.graph.nodes().forEach(function(n) {
                if (toKeep[n.id]) {
                    n.color = n.originalColor;
                    n.label = n.originalLabel;
                } else {
                    n.color = '#ccc';
                    n.label = '';
                }
            });

            s.graph.edges().forEach(function(e) {
                if (toKeep[e.source] && toKeep[e.target])
                    e.color = e.originalColor;
                else
                    e.color = '#ccc';
            });

            s.refresh();
        });

        s.bind('clickStage', function(e) {
            s.graph.nodes().forEach(function(n) {
                n.color = n.originalColor;
                n.label = n.originalLabel;
            });

            s.graph.edges().forEach(function(e) {
                e.color = e.originalColor;
            });

            s.refresh();
        });
    };

    var search = function(service, scope, rootScope) {
        scope.loading = true;
        scope.sigmaGraph.kill();

        service.search(scope.terms, Math.max(0, scope.pageNumber - 1), COUNT_PER_PAGE).then(
            function(response) {
                scope.loading   = false;
                rootScope.error = false;

                scope.results = response.data;
                minimizeCompounds(response.data.list);

                scope.sigmaGraph = new sigma({
                    graph: getGraph(response.data.list),
                    container: 'graph-container',
                });

                bindClickToSigma(scope.sigmaGraph);

                scope.refreshGraph();
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

            $scope.countPerPage = COUNT_PER_PAGE;
            $scope.maxSize      = MAX_PAGINATION_LINKS;
            $scope.pageNumber   = 1;
            $scope.sigmaGraph   = new sigma();

            $rootScope.pageTitle = $scope.terms + $rootScope.pageTitle;

            $scope.pageChanged = function( ) {
                search(SearchService, $scope, $rootScope);
                $window.scrollTo(0, 0);
            };

            $scope.goToDocument = function(d) {
                $rootScope.terms = $routeParams.terms;
                $location.path('/document/' + d.id).search('terms', null);
            };

            $scope.refreshGraph = function() {
                // nasty hack, sleep for 500ms before refreshing, needed
                // because the graph is inside a bootstrap tab, and does not
                // have proper sizes (required by sigma) until it is fully
                // rendered
                window.setTimeout(function() {
                    $scope.sigmaGraph.refresh();
                    window.dispatchEvent(new Event('resize'));
                }, 500);
            };

            search(SearchService, $scope, $rootScope);
        }
    ];

    controller.controller('SearchResultsController', searchResultsController);

});
