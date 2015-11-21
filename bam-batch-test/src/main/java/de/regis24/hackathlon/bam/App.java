package de.regis24.hackathlon.bam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.jsr.configuration.support.ThreadLocalClassloaderBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Hello world!
 */
@SpringBootApplication
@ComponentScan(basePackages = {"de.regis24"})
@EnableBatchProcessing
@EnableScheduling
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
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  public Job testJob1() {
    return jobBuilder.get("testJob1")
        .incrementer(new RunIdIncrementer())
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
        .incrementer(new RunIdIncrementer())
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
        .<Insolvency, Insolvency>chunk(1)
        .reader(insolvencyReader)
        .processor(insolvencyProcessor)
        .writer(insolvencyWriter)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  public Step movementStep() {
    return stepBuilder.get("movementStep")
        .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  public Step courtStep() {
    return stepBuilder.get("courtStep")
        .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  public Step cleanupStep() {
    return stepBuilder.get("cleanupStep")
        .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  public ItemReader<Insolvency> insolvencyReader() {
    FlatFileItemReader<Insolvency> reader = new FlatFileItemReader<>();
    reader.setResource(new ClassPathResource("insolvency100.csv"));
    reader.setLineMapper(new DefaultLineMapper<Insolvency>() {{
      setLineTokenizer(new DelimitedLineTokenizer() {{
        setNames(new String[]{"name"});
      }});
      setFieldSetMapper(new BeanWrapperFieldSetMapper<Insolvency>() {{
        setTargetType(Insolvency.class);
      }});
    }});
    return reader;
  }

  @Bean
  public ItemProcessor<Insolvency, Insolvency> insolvencyProcessor() {
    return i -> {
      Random random = new Random();
      Thread.sleep(random.nextInt(50));
      return i;
    };
  }

  @Bean
  public ItemWriter<Insolvency> insolvencyWriter() {
    return i -> {
      Random random = new Random();
      Thread.sleep(random.nextInt(50));
    };
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Data
  @NoArgsConstructor
  public static class Insolvency {

    private String name;
  }

}
