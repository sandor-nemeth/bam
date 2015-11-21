angular.module 'bamUiAngularCoffee'
  .controller 'MainController', (jobs) ->
    'ngInject'
    vm = this
    activate = ->
      jobs.stats().success (data) ->
        vm.stats = data
        return
      jobs.jobStats().success (data) ->
        vm.jobStats = data
        return
      return

    activate()
    return
