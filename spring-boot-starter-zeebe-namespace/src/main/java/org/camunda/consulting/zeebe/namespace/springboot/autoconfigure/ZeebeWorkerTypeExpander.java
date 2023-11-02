package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure;

import java.util.function.Function;

public class ZeebeWorkerTypeExpander {

    private String resolvedTagValue = "";

    private String typeDelimiter = "";

    private Function<String, String> expander;

    public ZeebeWorkerTypeExpander() {
    }

    public String getResolvedTagValue() {
        return resolvedTagValue;
    }

    public void setResolvedTagValue(String resolvedTagValue) {
        this.resolvedTagValue = resolvedTagValue;
    }

    public String getTypeDelimiter() {
        return typeDelimiter;
    }

    public void setTypeDelimiter(String typeDelimiter) {
        this.typeDelimiter = typeDelimiter;
    }

    public void detectExpander(ZeebeWorkerTypeModeEnum expanderMode) {
        expander = switch (expanderMode) {
            case OFF, EXPLICIT -> this::identity;
            case AUTO -> this::auto;
            case PREFIX -> this::prefix;
            case POSTFIX -> this::postfix;
        };
    }

    public Function<String, String> getExpander() {
        return expander;
    }

    public String expandTypeValue(String typeValue) {
        String result = typeValue;
        if (expander != null) {
            result = expander.apply(typeValue);
        }
        return result;
    }

    public String identity(String typeValue) {
        return typeValue;
    }

    public String prefix(String typeValue) {
        return resolvedTagValue + typeDelimiter + typeValue;
    }

    public String postfix(String typeValue) {
        return typeValue + typeDelimiter + resolvedTagValue;
    }

    public String auto(String typeValue) {
        StringBuilder buf = new StringBuilder();
        if (typeValue.startsWith(typeDelimiter)) {
            buf.append(resolvedTagValue);
        }
        buf.append(typeValue);
        if (typeValue.endsWith(typeDelimiter)) {
            buf.append(resolvedTagValue);
        }
        return buf.toString();
    }
}
