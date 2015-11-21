angular.module 'bamUiAngularCoffee'
  .directive 'lteFooter', ->

    LteFooterController = (moment) ->
      'ngInject'
      vm = this
      return

    directive =
      restrict: 'E'
      templateUrl: 'app/components/adminlte/footer/footer.html'
      scope: creationDate: '='
      controller: LteFooterController
      controllerAs: 'vm'
      bindToController: true
