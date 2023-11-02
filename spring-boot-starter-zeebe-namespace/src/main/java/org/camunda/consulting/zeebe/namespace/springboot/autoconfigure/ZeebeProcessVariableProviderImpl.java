package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure;

import java.util.HashMap;
import java.util.Map;

public class ZeebeProcessVariableProviderImpl implements ZeebeProcessVariableProvider {

    private Map<String, Object> processVariables;

    public ZeebeProcessVariableProviderImpl(Map<String, Object> procVars) {
        processVariables = procVars;
    }

    public Map<String, Object> baseVariables() {
        return new HashMap<>(processVariables);
    }
}
