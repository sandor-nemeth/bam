package de.regis24.hackathlon.bam.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class BamJdbcDao {

  private static final String QUERY_NUMBER_OF_EXECUTIONS =
      "SELECT count(*) FROM %PREFIX%JOB_EXECUTION";
  private static final String QUERY_NUMBER_OF_WRITTEN_ITEMS =
      "SELECT sum(WRITE_COUNT) FROM %PREFIX%STEP_EXECUTION";
  private static final String QUERY_TOTAL_EXECUTION_TIME =
      "SELECT sum(a) FROM (SELECT datediff('SECOND', START_TIME, END_TIME) as a FROM "
      + "%PREFIX%JOB_EXECUTION)";

  @Autowired
  private JdbcOperations jdbcTemplate;

  private String getQuery(String base) {
    return StringUtils.replace(base, "%PREFIX%", "BATCH_");
  }

  public int getNumberOfExecutions() {
    return jdbcTemplate.queryForObject(getQuery(QUERY_NUMBER_OF_EXECUTIONS), Integer.class);
  }

  public int getNumberOfItemsImported() {
    return jdbcTemplate.queryForObject(getQuery(QUERY_NUMBER_OF_WRITTEN_ITEMS), Integer.class);
  }

  public int getTotalExecutionTime() {
    return jdbcTemplate.queryForObject(getQuery(QUERY_TOTAL_EXECUTION_TIME), Integer.class);
  }

}
