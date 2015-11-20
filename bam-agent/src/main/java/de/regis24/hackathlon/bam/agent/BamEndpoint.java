package de.regis24.hackathlon.bam.agent;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@RestController
@RequestMapping("/bam")
public class BamEndpoint {

  @Autowired(required = false)
  private List<Job> jobs = new ArrayList<>();
  @Autowired private BamJdbcDao bamJdbcDao;

  @RequestMapping("jobs")
  public List<JobDescription> jobs() {
    return jobs.stream()
        .map(JobDescription::fromJob)
        .collect(Collectors.toList());
  }

  @RequestMapping("stats")
  public Stats stats() {
    return Stats.builder()
        .numberOfExecutions(bamJdbcDao.getNumberOfExecutions())
        .numberOfProcessedItems(bamJdbcDao.getNumberOfItemsImported())
        .totalExecutedTime(bamJdbcDao.getTotalExecutionTime())
        .build();
  }

  @Getter @Builder
  public static class Stats {
    private int numberOfExecutions;
    private int numberOfProcessedItems;
    private long totalExecutedTime;
  }

}
