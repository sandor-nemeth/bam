angular.module 'bamUiAngularCoffee'
  .service 'jobInstance', ($http) ->
    'ngInject'

    getJobInstances = ->
      request = $http.get '/jobInstances.json', params: {  }

    @getJobInstances = getJobInstances
    return
