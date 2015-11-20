(function() {
  'use strict';

  angular
    .module('bamUiAngular')
    .run(runBlock);

  /** @ngInject */
  function runBlock($log) {

    $log.debug('runBlock end');
  }

})();
