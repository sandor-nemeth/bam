angular.module 'bamUiAngularCoffee'
  .directive 'lteContent', ->

    LteContentController = (jobInstance, $log) ->
      'ngInject'

      activate = ->
        getJobInstances()

      vm = this



      getJobInstances = ->
        jobInstance.getJobInstances().success (data) ->
          vm.jobInstances = data
          $log.debug vm.jobInstances
          return
        return

      activate()
      return

    directive =
      restrict: 'E'
      templateUrl: 'app/components/adminlte/content/content.html'
      scope: creationDate: '='
      controller: LteContentController
      controllerAs: 'vm'
      bindToController: true
