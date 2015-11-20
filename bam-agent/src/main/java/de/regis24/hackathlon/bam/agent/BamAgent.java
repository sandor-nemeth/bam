package de.regis24.hackathlon.bam.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

public class BamAgent {

  @Component
  public static class StartEventListener implements ApplicationListener<ContextStartedEvent> {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${bam.url}")
    private String bamUrl;

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
      restTemplate.put(bamUrl, null);
    }
  }

  @Component
  public static class StopEventListener implements ApplicationListener<ContextStoppedEvent> {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${bam.url}")
    private String bamUrl;

    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
      restTemplate.delete(bamUrl);
    }
  }

}
