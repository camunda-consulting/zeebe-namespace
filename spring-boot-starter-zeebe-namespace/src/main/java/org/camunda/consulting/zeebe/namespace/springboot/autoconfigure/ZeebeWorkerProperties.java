package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix="zeebe.namespace")
public class ZeebeWorkerProperties {

    private Log log = LogFactory.getLog(ZeebeWorkerProperties.class);
    private String variableStart = "${";

    private String variableEnd = "}";

    private String typeDelimiter = "_";

    private ZeebeWorkerTypeModeEnum typeMode = ZeebeWorkerTypeModeEnum.AUTO;

    private ZeebeWorkerVariableModeEnum variableMode = ZeebeWorkerVariableModeEnum.ALL;

    private String tagName = "pt";

    private List<String> tagVariableNames = new ArrayList<>(List.of("processKey", "version"));

    private Map<String, String> mappings = new HashMap<>();

    private Integer defaultMaxJobsActive = Integer.valueOf(-1);

    private ZeebeWorkerTagValueDelimiterMode tagValueDelimiterMode = ZeebeWorkerTagValueDelimiterMode.DEPENDS_TYPE_MODE;

    public ZeebeWorkerProperties() {
    }

    public String getVariableStart() {
        return variableStart;
    }

    public void setVariableStart(String variableStart) {
        this.variableStart = variableStart;
    }

    public String getVariableEnd() {
        return variableEnd;
    }

    public void setVariableEnd(String variableEnd) {
        this.variableEnd = variableEnd;
    }

    public String getTypeDelimiter() {
        return typeDelimiter;
    }

    public void setTypeDelimiter(String typeDelimiter) {
        this.typeDelimiter = typeDelimiter;
    }

    public ZeebeWorkerTypeModeEnum getTypeMode() {
        return typeMode;
    }

    public void setTypeMode(ZeebeWorkerTypeModeEnum typeMode) {
        this.typeMode = typeMode;
    }

    public ZeebeWorkerVariableModeEnum getVariableMode() {
        return variableMode;
    }

    public void setVariableMode(ZeebeWorkerVariableModeEnum variableMode) {
        this.variableMode = variableMode;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<String> getTagVariableNames() {
        return tagVariableNames;
    }

    public void setTagVariableNames(List<String> tagVariableNames) {
        this.tagVariableNames = tagVariableNames;
    }

    public Map<String, String> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }

    public Integer getDefaultMaxJobsActive() {
        return defaultMaxJobsActive;
    }

    public void setDefaultMaxJobsActive(Integer defaultMaxJobsActive) {
        this.defaultMaxJobsActive = defaultMaxJobsActive;
    }

    public ZeebeWorkerTagValueDelimiterMode getTagValueDelimiterMode() {
        return tagValueDelimiterMode;
    }

    public void setTagValueDelimiterMode(ZeebeWorkerTagValueDelimiterMode tagValueDelimiterMode) {
        this.tagValueDelimiterMode = tagValueDelimiterMode;
    }
}
