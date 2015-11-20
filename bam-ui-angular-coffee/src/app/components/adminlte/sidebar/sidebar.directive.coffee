angular.module 'bamUiAngularCoffee'
  .directive 'lteSidebar', ->

    LteSidebarController = (moment) ->
      'ngInject'
      vm = this
      return

    directive =
      restrict: 'E'
      templateUrl: 'app/components/adminlte/sidebar/sidebar.html'
      scope: creationDate: '='
      controller: LteSidebarController
      controllerAs: 'vm'
      bindToController: true
