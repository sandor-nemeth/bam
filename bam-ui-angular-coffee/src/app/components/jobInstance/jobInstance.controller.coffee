angular.module 'bamUiAngularCoffee'
  .controller 'JobInstanceController', (jobInstance, $stateParams) ->
    'ngInject'
    vm = this
    activate = ->
      vm.host = $stateParams.host
      vm.jobName = $stateParams.jobName
      getDetails()
      return

    getDetails = ->
      jobInstance.getJobDetails($stateParams.host, $stateParams.jobName).success (data) ->
        vm.details = data 
      return

    activate()
    return
