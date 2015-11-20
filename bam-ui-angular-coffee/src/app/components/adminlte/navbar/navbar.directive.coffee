angular.module 'bamUiAngularCoffee'
  .directive 'lteNavbar', ->

    LteNavbarController = (moment) ->
      'ngInject'
      vm = this
      vm.hello='Helloworld!'
      return

    directive =
      restrict: 'E'
      templateUrl: 'app/components/adminlte/navbar/navbar.html'
      scope: creationDate: '='
      controller: LteNavbarController
      controllerAs: 'vm'
      bindToController: true
