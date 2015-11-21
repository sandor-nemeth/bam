package de.regis24.hackathlon.bam;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!
 */
@SpringBootApplication
@ComponentScan(basePackages = {"de.regis24"})
@EnableBatchProcessing
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
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

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

}
