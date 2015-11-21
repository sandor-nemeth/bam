angular.module 'bamUiAngularCoffee'
  .service 'jobs', ($http) ->
    'ngInject'

    getStats = ->
      $http.get 'http://localhost:4000/stats', params: {}
    getLatestJobStats = ->
      $http.get 'http://localhost:4000/jobs', params: {}
    getJobDetails = (host, jobName)->
      str = host.split(':')
      url = 'http://localhost:4000/job/' + str[0] + '/' + str[1] + '/' + jobName
      $http.get url, params: {}

    @stats = getStats
    @jobStats = getLatestJobStats
    @jobDetails = getJobDetails
    return
