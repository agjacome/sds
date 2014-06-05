define(['./main'], function(controller) {
    'use strcit';

    var pubmedProviderController = [
        '$scope', '$location', '$rootScope', '$window', '$modal', 'PubMedProviderService',
        function($scope, $location, $rootScope, $window, $modal, PubMedProviderService) {

        }
    ];

    controller.controller('PubMedProviderController', pubmedProviderController);

});
