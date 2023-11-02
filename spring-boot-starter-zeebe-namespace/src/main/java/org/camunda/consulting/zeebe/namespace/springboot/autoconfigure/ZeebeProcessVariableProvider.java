package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure;

import java.util.Map;

public interface ZeebeProcessVariableProvider {
    public Map<String, Object> baseVariables();
}
