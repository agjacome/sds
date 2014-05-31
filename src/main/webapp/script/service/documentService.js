define(['./main'], function(service) {
    'use strict';

    service.factory('DocumentService', ['$resource', function($resource) {
        return $resource('api/document/:id', { id : '@id' });
    }]);

});
