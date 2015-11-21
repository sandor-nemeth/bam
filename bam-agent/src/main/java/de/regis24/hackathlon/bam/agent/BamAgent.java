package de.regis24.hackathlon.bam.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

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

  @Value("${server.port}")
  private int port;

  @PostConstruct
  public void start() {
    System.out.println("REGISTER!");
    try {
      InetAddress host = InetAddress.getLocalHost();
      restTemplate.put(bamUrl, new BamServerInfo(host.getHostAddress(), port));
    } catch (Exception e) {
      // should not happen.
    }
  }

  @PreDestroy
  public void stop() {
    try {
      InetAddress host = InetAddress.getLocalHost();
      String url = bamUrl + "/" + host.getHostAddress() + "/" + port;
      System.out.println("DEREGISTER!");
      restTemplate.delete(url);
    } catch (Exception e) {
      // should not happen.
    }
  }

}
