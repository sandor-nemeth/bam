package de.regis24.hackathlon.bam.agent;

import org.springframework.batch.core.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bam")
public class BamEndpoint {

  @Autowired
  private List<Job> jobs;

  @RequestMapping("jobs")
  public List<JobDescription> jobs() {
    return jobs.stream()
        .map(JobDescription::fromJob)
        .collect(Collectors.toList());
  }

}
