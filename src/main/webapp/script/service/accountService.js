define(['./main'], function(service) {
    'use strict';

    service.factory('AccountService', ['$resource', function($resource) {
        return $resource('api/account/:id', { id : '@id' }, {
            query : { isArray : false },
        });
    }]);

});
