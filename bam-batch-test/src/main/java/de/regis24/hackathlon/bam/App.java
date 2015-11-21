package de.regis24.hackathlon.bam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import de.regis24.hackathlon.bam.App.Insolvency;
import lombok.Builder;
import lombok.Data;

/**
 * Hello world!
 */
@SpringBootApplication
@ComponentScan(basePackages = {"de.regis24"})
@EnableBatchProcessing
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);

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
  public Job insolvencyImportJob(
		  @Qualifier("insolvencyStep") Step insolvencySteb,
		  @Qualifier("movementStep") Step movementStep,
		  @Qualifier("courtStep") Step courtStep,
		  @Qualifier("cleanupStep") Step cleanupStep) {
    return jobBuilder.get("insolvencyImportJob")
        .start(insolvencySteb)
        .next(movementStep)
        .next(courtStep)
        .next(cleanupStep)
        .build();
  }
  
  @Bean  
  public Step insolvencyStep(
		  @Qualifier("insolvencyReader") ItemReader<Insolvency> insolvencyReader, 
		  @Qualifier("insolvencyProcessor") ItemProcessor<Insolvency, Insolvency> insolvencyProcessor,
		  @Qualifier("insolvencyWriter") ItemWriter<Insolvency> insolvencyWriter) {
    return stepBuilder.get("insolvencyStep")
    	.<Insolvency, Insolvency> chunk(1)
    	.reader(insolvencyReader)    	
    	.processor(insolvencyProcessor)
    	.writer(insolvencyWriter)
    	.build();
  }
  
  @Bean
  public Step movementStep() {
    return stepBuilder.get("movementStep")
        .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
        .build();
  }  
  
  @Bean
  public Step courtStep() {
    return stepBuilder.get("courtStep")
        .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
        .build();
  }
  
  @Bean
  public Step cleanupStep() {
    return stepBuilder.get("cleanupStep")
        .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
        .build();
  }
  
  @Bean
  public ItemReader<Insolvency> insolvencyReader(){
	  return () -> {
		  logger.info("reading insolvency");
		  Thread.sleep(1000);
		  return Insolvency.builder().build();
	};
  }
  
  @Bean
  public ItemProcessor<Insolvency, Insolvency> insolvencyProcessor(){
	  return i -> {
		  logger.info("processing insolvency");
		  Thread.sleep(500);
		  return i;
	  };
  }
  
  @Bean
  public ItemWriter<Insolvency> insolvencyWriter(){
	  return  i -> {
		  logger.info("writing insolvency");
		  Thread.sleep(1500);
	};
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
  
  @Data
  @Builder
  public static class Insolvency{
	  
  }

}
