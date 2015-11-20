(function() {
  'use strict';

  angular
    .module('bamUiAngular')
    .directive('adminlteNavbar', adminlteNavbar);

  /** @ngInject */
  function adminlteNavbar() {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/adminlte/navbar/navbar.html',
      scope: {
          creationDate: '='
      },
      controller: AdminLteNavbarController,
      controllerAs: 'vm',
      bindToController: true
    };

    return directive;

    /** @ngInject */
    function AdminLteNavbarController(moment) {
      var vm = this;

      // "vm.creation" is avaible by directive option "bindToController: true"
      vm.relativeDate = moment(vm.creationDate).fromNow();
    }
  }

})();
