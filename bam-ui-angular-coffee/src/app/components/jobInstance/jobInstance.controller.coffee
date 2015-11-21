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
      vm.jobInstance = jobInstance
      return

    activate()
    return

