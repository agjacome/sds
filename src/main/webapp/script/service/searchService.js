define(['./main'], function(service) {
    'use strict';

    service.factory('SearchService', ['$http', function($http) {
        return {
            search : function(terms, page, size) {
                return $http.get('api/search', {
                    params : {
                        query : terms,
                        page  : page,
                        size  : size
                    }
                });
            }
        }
    }]);

});
