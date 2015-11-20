package de.regis24.hackathlon.bam.agent;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bam")
public class BamEndpoint {

  @Autowired private JobRegistry jobRegistry;

  @RequestMapping("jobs")
  public List<JobDescription> jobs() {
    return jobRegistry.getJobNames().stream()
        .map(jobName -> {
          try {
            return jobRegistry.getJob(jobName);
          } catch (NoSuchJobException e) {
            throw new RuntimeException(e);
          }
        })
        .map(job -> new JobDescription(job.getName(), job.isRestartable()))
        .collect(Collectors.toList());
  }

}
