package de.regis24.hackathlon.bam.agent;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.batch.operations.JobOperator;

import lombok.Builder;
import lombok.Getter;

@RestController
@RequestMapping("/bam")
public class BamEndpoint {

  @Autowired(required = false)
  private List<Job> jobs = new ArrayList<>();
  @Autowired
  private BamJdbcDao bamJdbcDao;
  @Autowired
  private JobExplorer jobExplorer;
  @Autowired
  private JobLauncher jobLauncher;
  @Autowired
  private ApplicationContext ctx;

  @RequestMapping("stats")
  public Stats stats() {
    return Stats.builder()
        .numberOfExecutions(bamJdbcDao.getNumberOfExecutions())
        .numberOfProcessedItems(bamJdbcDao.getNumberOfItemsImported())
        .totalExecutedTime(bamJdbcDao.getTotalExecutionTime())
        .build();
  }

  @RequestMapping("jobs")
  public List<JobStats> jobStats() {
    return jobs.stream()
        .map(this::lastJobExecution)
        .filter(jobExecution -> jobExecution != null)
        .map(this::toJobStats)
        .collect(Collectors.toList());
  }

  @RequestMapping("job/{jobName}")
  public List<BamJobExecution> singleJob(@PathVariable("jobName") String jobName)
      throws NoSuchJobException {
    List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName, 0, 1);
    if (!CollectionUtils.isEmpty(jobInstances)) {
      List<JobExecution> executions = jobExplorer.getJobExecutions(jobInstances.get(0));
      return executions.stream().map(BamJobExecution::fromJobExecution)
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }
  
  @RequestMapping("job/{jobName}/run")
  public void runJob(@PathVariable("jobName") String jobName){
	  Job job = (Job) ctx.getBean(jobName);
	  try {
		jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
	} catch (Exception e) {
	}
  }

  private JobStats toJobStats(JobExecution jobExecution) {
    int skippedItems = 0;
    int writtenItems = 0;
    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
      skippedItems += stepExecution.getReadSkipCount()
                      + stepExecution.getProcessSkipCount()
                      + stepExecution.getWriteSkipCount();
      writtenItems += stepExecution.getWriteCount();
    }

    return JobStats.builder()
        .jobName(jobExecution.getJobInstance().getJobName())
        .numberOfSkippedItems(skippedItems)
        .numberOfWrittenItems(writtenItems)
        .resultOfLastExecution(jobExecution.getExitStatus().getExitCode())
        .build();
  }

  private JobExecution lastJobExecution(Job job) {
    List<JobInstance> jobInstances = jobExplorer.getJobInstances(job.getName(), 0, 1);
    if (!CollectionUtils.isEmpty(jobInstances)) {
      List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstances.get(0));
      if (!CollectionUtils.isEmpty(jobExecutions)) {
        return jobExecutions.get(0);
      }
    }
    return null;
  }

  @Getter
  @Builder
  public static class Stats {

    private int numberOfExecutions;
    private int numberOfProcessedItems;
    private long totalExecutedTime;
  }

  @Getter
  @Builder
  public static class JobStats {

    String jobName;
    int numberOfWrittenItems;
    int numberOfSkippedItems;
    String resultOfLastExecution;
  }

  @Getter
  public static class BamJobExecution {

    public static final BamJobExecution fromJobExecution(JobExecution jobExecution) {
      BamJobExecution execution = new BamJobExecution();
      execution.jobParameters = jobExecution.getJobParameters();
      execution.jobInstance = jobExecution.getJobInstance();
      execution.status = jobExecution.getStatus();
      execution.startTime = jobExecution.getStartTime();
      execution.endTime = jobExecution.getEndTime();
      execution.lastUpdated = jobExecution.getLastUpdated();
      execution.exitStatus = jobExecution.getExitStatus();
      execution.executionContext = jobExecution.getExecutionContext();
      execution.failureExceptions = jobExecution.getFailureExceptions();
      execution.jobConfigurationName = jobExecution.getJobConfigurationName();
      execution.stepExecutions = jobExecution.getStepExecutions().stream()
          .map(BamStepExecution::fromStepExecution)
          .collect(Collectors.toSet());
      return execution;
    }

    private JobParameters jobParameters;
    private JobInstance jobInstance;
    private Collection<BamStepExecution> stepExecutions = new HashSet<>();
    private BatchStatus status = BatchStatus.STARTING;
    private Date startTime = null;
    private Date createTime = new Date(System.currentTimeMillis());
    private Date endTime = null;
    private Date lastUpdated = null;
    private ExitStatus exitStatus = ExitStatus.UNKNOWN;
    private ExecutionContext executionContext = new ExecutionContext();
    private List<Throwable> failureExceptions = new ArrayList<>();
    private String jobConfigurationName;
  }

  @Getter
  public static class BamStepExecution {

    public static final BamStepExecution fromStepExecution(StepExecution stepExecution) {
      BamStepExecution execution = new BamStepExecution();
      execution.stepName = stepExecution.getStepName();
      execution.status = stepExecution.getStatus();
      execution.readCount = stepExecution.getReadCount();
      execution.writeCount = stepExecution.getWriteCount();
      execution.commitCount = stepExecution.getCommitCount();
      execution.readSkipCount = stepExecution.getReadSkipCount();
      execution.rollbackCount = stepExecution.getRollbackCount();
      execution.processSkipCount = stepExecution.getProcessSkipCount();
      execution.writeSkipCount = stepExecution.getWriteSkipCount();
      execution.startTime = stepExecution.getStartTime();
      execution.endTime = stepExecution.getEndTime();
      execution.lastUpdated = stepExecution.getLastUpdated();
      execution.executionContext = stepExecution.getExecutionContext();
      execution.exitStatus = stepExecution.getExitStatus();
      execution.terminateOnly = stepExecution.isTerminateOnly();
      execution.filterCount = stepExecution.getFilterCount();
      execution.failureExceptions = stepExecution.getFailureExceptions();
      return execution;
    }

    private String stepName;

    private BatchStatus status = BatchStatus.STARTING;

    private int readCount = 0;

    private int writeCount = 0;

    private int commitCount = 0;

    private int rollbackCount = 0;

    private int readSkipCount = 0;

    private int processSkipCount = 0;

    private int writeSkipCount = 0;

    private Date startTime = new Date(System.currentTimeMillis());

    private Date endTime = null;

    private Date lastUpdated = null;

    private ExecutionContext executionContext = new ExecutionContext();

    private ExitStatus exitStatus = ExitStatus.EXECUTING;

    private boolean terminateOnly;

    private int filterCount;

    private List<Throwable> failureExceptions = new CopyOnWriteArrayList<Throwable>();
  }
}
