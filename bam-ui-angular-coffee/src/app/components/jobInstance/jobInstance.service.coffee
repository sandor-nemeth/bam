angular.module 'bamUiAngularCoffee'
  .service 'jobInstance', ($http) ->
    'ngInject'

    getStats = ->
      $http.get 'http://localhost:4000/stats', params: {}

    getJobInstances = ->
      $http.get '/jobInstances.json', params: {  }

    getJobDetails = (host, jobName)->
      $http.get '/jobDetails.json', params: {}

    @getJobInstances = getJobInstances
    @getJobDetails = getJobDetails
    @stats = getStats
    return
