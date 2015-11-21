angular.module 'bamUiAngularCoffee'
  .controller 'JobInstanceController', (jobs, $stateParams) ->
    'ngInject'
    vm = this
    activate = ->
      vm.host = $stateParams.host
      vm.jobName = $stateParams.jobName
      getDetails()
      return

    getDetails = ->
      jobs.jobDetails($stateParams.host, $stateParams.jobName).success (data) ->
        vm.details = data
        allProcessedItems()
        numberOfStepExecutions()
      return

    numberOfStepExecutions = ->
      stepExecutions = 0
      stepExecutions = vm.details[vm.details.length - 1].stepExecutions.length if vm.details.length
      vm.numberOfSteps = stepExecutions

    allProcessedItems = ->
      processedItems = 0
      angular.forEach vm.details, (v) ->
        processedItems += step.writeCount for step in v.stepExecutions
        return
      vm.totalItemsProcessed = processedItems

    processedItems = (jobExecution) ->
      processedItems = 0
      processedItems += v.writeCount for v in jobExecution.stepExecutions
      processedItems

    skippedItems = (jobExecution) ->
      skippedItems = 0
      skippedItems += v.readSkipCount + v.processSkipCount + v.writeSkipCount for v in jobExecution.stepExecutions
      skippedItems

    vm.processedItems = processedItems
    vm.skippedItems = skippedItems
    activate()
    return
