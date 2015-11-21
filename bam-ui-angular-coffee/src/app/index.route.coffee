angular.module 'bamUiAngularCoffee'
  .config ($stateProvider, $urlRouterProvider) ->
    'ngInject'
    $stateProvider
      .state 'home',
        url: '/'
        templateUrl: 'app/main/main.html'
        controller: 'MainController'
        controllerAs: 'main'
      .state 'jobDetails',
        url: '/job-details/:host/:jobName'
        templateUrl: 'app/components/jobInstance/jobDetails.html'
        controller: 'JobInstanceController'
        controllerAs: 'job'

    $urlRouterProvider.otherwise '/'
