angular.module 'bamUiAngularCoffee'
  .service 'jobInstance', ($http) ->
    'ngInject'

    getJobInstances = ->
      $http.get '/jobInstances.json', params: {  }

    getJobDetails = (host, jobName)->
      $http.get '/jobDetails.json', params: {}

    @getJobInstances = getJobInstances
    @getJobDetails = getJobDetails
    return
