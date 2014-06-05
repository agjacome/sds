define(['./main'], function(service) {
    'use strict';

    service.factory('PubMedProviderService', ['$http', function($http) {
        return {
            search : function(terms, limit, count, start) {
                return $http.get('api/provider/pubmed/search', {
                    params : {
                        query : query,
                        limit : limit,
                        count : count,
                        start : start,
                    }
                });
            },
            download : function(ids) {
                return $http.post('api/provider/pubmed/download', ids);
            },
        }
    }]);

});
