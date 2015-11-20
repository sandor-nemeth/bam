package de.regis24.hackathlon.bam.agent;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;

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

  @Pact(provider = "bamProvider", consumer = "bamAgent")
  public PactFragment createFragment(PactDslWithProvider builder) {
    return builder
        .given("no state")
        .uponReceiving("Register service")
        .path("/agent")
        .method("PUT")
        .willRespondWith()
        .status(200)
        .uponReceiving("Remove service")
        .path("/agent")
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
    listener.start();
    listener.stop();
  }

}
