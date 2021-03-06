package de.regis24.hackathlon.bam.agent;

import org.mockito.Mockito;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author sandornemeth
 */
@SpringBootApplication
@EnableBatchProcessing
public class TestConfigApp {

  @Bean public RestTemplate restTemplate() {
    return Mockito.mock(RestTemplate.class);
  }


  @Autowired
  private JobBuilderFactory jobBuilder;
  @Autowired
  private StepBuilderFactory stepBuilder;

  @Bean
  public Step tasklet() {
    return stepBuilder.get("step")
        .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
        .build();
  }

  @Bean
  public Job testJob1() {
    return jobBuilder.get("testJob1")
        .start(tasklet())
        .build();
  }

}
