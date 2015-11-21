package de.regis24.hackathlon.bam.agent;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
}
