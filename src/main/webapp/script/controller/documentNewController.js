define(['./main'], function(controller) {

    var createDocument = function(service, scope, rootScope, location) {
        scope.loading = true;
        service.save(scope.document).$promise.then(
            function(data) {
               scope.loading   = false;
               rootScope.error = false;
               location.path('/document/' + data.id);
            },
            function(response) {
                scope.loading          = false;
                rootScope.error        = true;
                rootScope.errorMessage = response.data.err;
            }
        );
    };

    var documentNewController = [
        '$scope', '$location', '$rootScope', 'DocumentService',
        function($scope, $location, $rootScope, DocumentService) {

            $scope.document = {
                title    : '',
                text     : '',
                pubmedId : null
            };

            $scope.createDocument = function( ) {
                createDocument(DocumentService, $scope, $rootScope, $location);
            };

        }
    ];

    controller.controller('DocumentNewController', documentNewController);

});
