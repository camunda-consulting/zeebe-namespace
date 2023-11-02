package org.camunda.consulting.dynamic_job_worker_example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

@Configuration
public class ExampleWorker {
  
  private static final Logger LOG = LoggerFactory.getLogger(ExampleWorker.class);

  @JobWorker(type = "_worker1")
  public void exampleWorker1() {
    LOG.info("ExampleWorker1 invoked");
  }
}
