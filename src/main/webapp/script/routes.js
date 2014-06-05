define(['app'], function(app) {
    'use strict';

    var checkAuthorization = [
        'AuthorizationService', function(AuthorizationService) {
            return AuthorizationService.ping().success(function(data) {
                AuthorizationService.setAccountId(data.accountId);
                return data;
            }).error(function(data) {
                AuthorizationService.setAccountId(undefined);
            });
        }
    ];

    var logoutAndRedirect = [
        '$location', 'AuthorizationService',
        function($location, AuthorizationService) {
            return AuthorizationService.logout().then(function( ) {
                AuthorizationService.setAccountId(undefined);
                $location.path('/');
            });
        }
    ];

    app.config([
        '$routeProvider', '$locationProvider',
        function($routeProvider, $locationProvider) {

            $locationProvider.hashPrefix('!');
            if (window.history && window.history.pushState)
                $locationProvider.html5Mode(true);

            $routeProvider.when('/', {
                pageTitle   : 'SmartDrugSearch',
                templateUrl : 'assets/template/home.html',
                controller  : 'HomeController',
            });

            $routeProvider.when('/search', {
                pageTitle   : ' :: SmartDrugSearch',
                templateUrl : 'assets/template/searchResults.html',
                controller  : 'SearchResultsController',
            });

            $routeProvider.when('/document/:id', {
                pageTitle      : ' :: Document :: SmartDrugSearch',
                templateUrl    : 'assets/template/documentShow.html',
                controller     : 'DocumentShowController',
                reloadOnSearch : false,
            });

            $routeProvider.when('/login', {
                pageTitle   : 'Sign In :: SmartDrugSearch',
                templateUrl : 'assets/template/login.html',
                controller  : 'AuthorizationController',
            });

            $routeProvider.when('/logout', {
                resolve : { logout : logoutAndRedirect }
            });

            $routeProvider.when('/admin', {
                redirectTo : '/admin/documents',
            });

            $routeProvider.when('/admin/documents', {
                pageTitle   : 'Documents :: Admin Panel :: SmartDrugSearch',
                templateUrl : 'assets/template/documentList.html',
                controller  : 'DocumentListController',
                resolve     : { authorize : checkAuthorization },
            });

            $routeProvider.when('/admin/document/new', {
                pageTitle   : 'New Document :: Admin Panel :: SmartDrugSearch',
                templateUrl : 'assets/template/documentNew.html',
                controller  : 'DocumentNewController',
                resolve     : { authorize : checkAuthorization },
            });

            $routeProvider.when('/admin/provider/pubmed', {
                pageTitle   : 'PubMed Provider :: Admin Panel :: SmartDrugSearch',
                templateUrl : 'assets/template/pubmedProvider.html',
                controller  : 'PubMedProviderController',
                resolve     : { authorize : checkAuthorization },
            });

            $routeProvider.when('/admin/accounts', {
                pageTitle   : 'Accounts :: Admin Panel :: SmartDrugSearch',
                templateUrl : 'assets/template/accountList.html',
                controller  : 'AccountListController',
                resolve     : { authorize : checkAuthorization },
            });

            $routeProvider.when('/admin/account/new', {
                pageTitle   : 'Create Account :: Admin Panel :: SmartDrugSearch',
                templateUrl : 'assets/template/accountNew.html',
                controller  : 'AccountNewController',
                resolve     : { authorize : checkAuthorization },
            });

            $routeProvider.when('/admin/account/edit/:id', {
                pageTitle   : ' :: Edit Account :: Admin Panel :: SmartDrugSearch',
                templateUrl : 'assets/template/accountEdit.html',
                controller  : 'AccountEditController',
                resolve     : { authorize : checkAuthorization },
            });

            $routeProvider.otherwise({ redirectTo : '/' });

        }
    ]);

})
