package org.camunda.consulting.dynamic_job_worker_example;

import java.util.Map;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.ZeebeProcessVariableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@RestController
public class ProcessStarter {
  
  private static final Logger LOG = LoggerFactory.getLogger(ProcessStarter.class);

  private ZeebeClient client;
  
  private ZeebeProcessVariableProvider valProvider;

  public ProcessStarter(ZeebeClient client, ZeebeProcessVariableProvider valProvider) {
    this.client = client;
    this.valProvider = valProvider;
  }

  @PostMapping("/start-example")
  public ResponseEntity<String> startExampleProcess(@RequestBody String businessKey) {
    LOG.info("starting process with businessKey: {}", businessKey);
    
    Map<String, Object> baseVariables = valProvider.baseVariables();
    baseVariables.put("businessKey", businessKey);
    ProcessInstanceEvent processInstanceEvent = client
        .newCreateInstanceCommand()
        .bpmnProcessId("processOne_1_0_Example1Process")
        .latestVersion()
        .variables(baseVariables)
        .send()
        .join();
    
    return ResponseEntity
        .status(HttpStatus.OK)
        .body("Started: " + processInstanceEvent.getProcessInstanceKey());
  }

}
