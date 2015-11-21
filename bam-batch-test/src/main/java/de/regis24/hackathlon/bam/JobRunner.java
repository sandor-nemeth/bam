package de.regis24.hackathlon.bam;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author sandornemeth
 */
@Component
public class JobRunner {

  @Autowired
  @Qualifier("testJob1")
  private Job testJob;
  @Autowired
  @Qualifier("insolvencyImportJob")
  private Job insolvencyJob;
  @Autowired
  private JobLauncher jobLauncher;

  @Scheduled(fixedRate = 30000)
  public void runJobs() {
    try {
      jobLauncher.run(testJob, new JobParametersBuilder().toJobParameters());
    } catch (Exception e) {
      // do nothing
    }
    try {
      jobLauncher.run(insolvencyJob, new JobParametersBuilder().toJobParameters());
    } catch (Exception e) {
      // do nothing
    }
  }

}
