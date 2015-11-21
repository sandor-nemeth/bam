package de.regis24.hackathlon.bam.agent;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author sandornemeth
 */
public class AgentConsumerTest {

  @Rule
  public PactProviderRule mockProvider = new PactProviderRule("bamProvider", this);

  private int port = 8088;
  
  @Pact(provider = "bamProvider", consumer = "bamAgent")
  public PactFragment createFragment(PactDslWithProvider builder) throws UnknownHostException {
			InetAddress host = InetAddress.getLocalHost();
    return builder
        .given("no state")
        .uponReceiving("Register service")
        .path("/agent")
        .method("PUT")
        .willRespondWith()
        .status(200)
        .uponReceiving("Remove service")
        .path("/agent/" + host.getHostName() +"/" + port)
        .method("DELETE")
        .willRespondWith()
        .status(200)
        .toFragment();
  }

  @Test
  @PactVerification
  public void verifyInteractions() {
    BamAgent listener = new BamAgent();
    ReflectionTestUtils.setField(listener, "restTemplate", new RestTemplate());
    ReflectionTestUtils.setField(listener, "bamUrl", mockProvider.getConfig().url() + "/agent");
    ReflectionTestUtils.setField(listener, "port", port);
    listener.start();
    listener.stop();
  }

}
