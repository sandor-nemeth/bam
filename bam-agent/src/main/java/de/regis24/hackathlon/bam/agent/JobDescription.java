package de.regis24.hackathlon.bam.agent;

import org.springframework.batch.core.Job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sandornemeth
 */
@Getter
@Setter
@AllArgsConstructor
public class JobDescription {

  private String jobName;
  private boolean restartable;

  public static JobDescription fromJob(Job job) {
    return new JobDescription(job.getName(), job.isRestartable());
  }
}
