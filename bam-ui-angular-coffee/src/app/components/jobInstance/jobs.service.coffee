angular.module 'bamUiAngularCoffee'
  .service 'jobs', ($http) ->
    'ngInject'

    getStats = ->
      $http.get 'http://localhost:4000/stats', params: {}
    getLatestJobStats = ->
      $http.get 'http://localhost:4000/jobs', params: {}
    @stats = getStats
    @jobStats = getLatestJobStats
    return
