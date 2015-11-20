package de.regis24.hackathlon.bam.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author sandornemeth
 */
@Component
public class BamAgent {

  private static final Logger logger = LoggerFactory.getLogger(BamAgent.class);

  @Autowired
  private RestTemplate restTemplate;

  @Value("${bam.url}")
  private String bamUrl;

  @PostConstruct
  public void start() {
    System.out.println("REGISTER!");
    restTemplate.put(bamUrl, null);
  }

  @PreDestroy
  public void stop() {
    System.out.println("DEREGISTER!");
    restTemplate.delete(bamUrl);
  }

}
