package de.regis24.hackathlon.bam.agent;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author sandornemeth
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfigApp.class)
public class BamTest {

  @Autowired
  BamJdbcDao bamJdbcDao;

  @Test
  public void test() {
    int numberOfExecutions = bamJdbcDao.getNumberOfExecutions();
    int itemsImported = bamJdbcDao.getNumberOfItemsImported();
    int time = bamJdbcDao.getTotalExecutionTime();
    assertThat(numberOfExecutions, equalTo(1));
    assertThat(itemsImported, equalTo(0));

  }

}
