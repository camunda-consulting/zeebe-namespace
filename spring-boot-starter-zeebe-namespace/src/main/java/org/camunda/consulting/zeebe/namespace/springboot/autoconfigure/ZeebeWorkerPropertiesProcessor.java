package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.resolver.ZeebeWorkerTypeResolver;
import static org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.ZeebeWorkerTagValueDelimiterMode.*;
import java.util.HashMap;
import java.util.Map;

public class ZeebeWorkerPropertiesProcessor {

    private Log log = LogFactory.getLog(ZeebeWorkerPropertiesProcessor.class);
    private ZeebeWorkerProperties props;

    private String resolvedTag;

    private String resolvedBpmnTag;

    private ZeebeWorkerTypeExpander expander;

    private ZeebeWorkerTypeResolver resolver;

    private Map<String, String> variableValues;

    private ZeebeProcessVariableProvider variableProvider;

    public ZeebeWorkerPropertiesProcessor(ZeebeWorkerProperties props) {
        this.props = props;
        init();
    }

    protected void init() {
        resolveTag();
        calcVariableValues();
        detectExpander();
        configureResolver();
        createVariableProvider();
        log.info("Resolved tag value: '" + resolvedTag + "'");
    }

    protected void resolveTag() {
        resolvedTag = props.getTagVariableNames()
                .stream()
                .filter(props.getMappings()::containsKey)
                .map(props.getMappings()::get)
                .reduce((first, second) -> first + props.getTypeDelimiter() + second)
                .orElse("");
        resolvedBpmnTag = resolvedTag;
        if (props.getTagValueDelimiterMode() == AS_PREFIX ||
                (props.getTagValueDelimiterMode() == DEPENDS_TYPE_MODE && props.getTypeMode() == ZeebeWorkerTypeModeEnum.PREFIX)) {
            resolvedBpmnTag = resolvedTag + props.getTypeDelimiter();
        } else if (props.getTagValueDelimiterMode() == AS_POSTFIX ||
                (props.getTagValueDelimiterMode() == DEPENDS_TYPE_MODE && props.getTypeMode() == ZeebeWorkerTypeModeEnum.POSTFIX)) {
            resolvedBpmnTag = props.getTypeDelimiter() + resolvedTag;
        }
    }

    protected void detectExpander() {
        expander = new ZeebeWorkerTypeExpander();
        expander.setResolvedTagValue(resolvedTag);
        expander.setTypeDelimiter(props.getTypeDelimiter());
        expander.detectExpander(props.getTypeMode());
    }

    protected void configureResolver() {
        resolver = new ZeebeWorkerTypeResolver(props.getTypeMode(), props.getVariableStart(), props.getVariableEnd());
        resolver.setVariableValues(variableValues);
    }

    protected void calcVariableValues() {
        variableValues = new HashMap<>();
        variableValues.putAll(props.getMappings());
        variableValues.put(props.getTagName(), resolvedTag);
    }


    protected void createVariableProvider() {
        HashMap<String, Object> varMappings = new HashMap<>();
        if (!props.getVariableMode().equals(ZeebeWorkerVariableModeEnum.OFF)) {
            // We want variable mappings
            varMappings.put(props.getTagName(), resolvedBpmnTag);
            if (props.getVariableMode().equals(ZeebeWorkerVariableModeEnum.FULLTAG)) {
                // We want more than the calculated variable
                props.getTagVariableNames()
                        .stream()
                        .filter(props.getMappings()::containsKey)
                        .forEach(key -> {
                            varMappings.put(key, props.getMappings().get(key));
                        });
            } else if (props.getVariableMode().equals(ZeebeWorkerVariableModeEnum.ALL)) {
                varMappings.putAll(props.getMappings());
            }
        }
        variableProvider = new ZeebeProcessVariableProviderImpl(varMappings);
    }

    public ZeebeWorkerTypeExpander getExpander() {
        return expander;
    }

    public ZeebeWorkerTypeResolver getResolver() {
        return resolver;
    }

    public ZeebeWorkerProperties getProperties() {
        return this.props;
    }

    public ZeebeProcessVariableProvider getProcessVariableProvider() {
        return this.variableProvider;
    }
}
