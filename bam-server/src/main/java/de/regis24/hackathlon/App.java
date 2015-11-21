package de.regis24.hackathlon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Hello world!
 */
@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @RestController
  public static class Registry {

    private Set<ServerInfo> agents = new HashSet<>();

    @RequestMapping(value = {"/agent"}, method = RequestMethod.GET, produces = MediaType
        .APPLICATION_JSON_VALUE)
    public Set<ServerInfo> list() {
      return agents;
    }

    @RequestMapping(value = {
        "/agent"}, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void register(@RequestBody ServerInfo serverInfo) {
      agents.add(serverInfo);
    }

    @RequestMapping(value = {"/agent/{ip}/{port}"}, method = RequestMethod.DELETE)
    public void deregister(@PathVariable("ip") String ip, @PathVariable("port") int port) {
      agents.remove(new ServerInfo(ip, port));
    }
  }

  @RestController
  @CrossOrigin("*")
  public static class StatsController {

    @Autowired
    private Registry registry;
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "stats", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Stats stats() {
      Stats globalStats = new Stats();

      for (ServerInfo agent : registry.agents) {
        Stats agentStats = restTemplate
            .getForObject(url(agent, "/bam/stats"), Stats.class);
        globalStats.numberOfExecutions += agentStats.numberOfExecutions;
        globalStats.numberOfProcessedItems += agentStats.numberOfProcessedItems;
        globalStats.totalExecutedTime += agentStats.totalExecutedTime;
      }
      return globalStats;
    }

    @RequestMapping(value = "jobs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JobStats> jobStats() {
      List<JobStats> jobStats = new ArrayList<>();
      for (ServerInfo agent : registry.agents) {
        JobStats[] stats = restTemplate.getForObject(url(agent, "/bam/jobs"), JobStats[].class);
        jobStats.addAll(Arrays.stream(stats).map(s -> {
          s.host = agent.host;
          return s;
        }).collect(Collectors.toList()));
      }
      return jobStats;
    }

    private String url(ServerInfo agent, String target) {
      return "http://" + agent.getHost() + ":" + agent.getPort() + target;
    }
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class Stats {

    private int numberOfExecutions;
    private int numberOfProcessedItems;
    private long totalExecutedTime;
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class JobStats {

    String jobName;
    String host;
    int numberOfWrittenItems;
    int numberOfSkippedItems;
    String resultOfLastExecution;
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class ServerInfo {

    String host;
    int port;
  }
}
