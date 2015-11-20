package de.regis24.hackathlon.bam.agent;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;

import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.mock;

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
    BamAgent.StartEventListener listener = new BamAgent.StartEventListener();
    ReflectionTestUtils.setField(listener, "restTemplate", new RestTemplate());
    ReflectionTestUtils.setField(listener, "bamUrl", mockProvider.getConfig().url() + "/agent");
    listener.onApplicationEvent(new ContextStartedEvent(mock(ApplicationContext.class)));

    BamAgent.StopEventListener stopListener = new BamAgent.StopEventListener();
    ReflectionTestUtils.setField(stopListener, "restTemplate", new RestTemplate());
    ReflectionTestUtils
        .setField(stopListener, "bamUrl", mockProvider.getConfig().url() + "/agent");
    stopListener.onApplicationEvent(new ContextStoppedEvent(mock(ApplicationContext.class)));
  }

}
