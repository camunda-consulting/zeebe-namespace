package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure;

import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.resolver.ZeebeWorkerTypeResolver;
import java.util.Map;

public class ZeebeJobWorkerTypeCustomizer implements ZeebeWorkerValueCustomizer {

    private Log log = LogFactory.getLog(ZeebeJobWorkerTypeCustomizer.class);

    private ZeebeWorkerTypeExpander expander;

    private ZeebeWorkerTypeResolver resolver;

    private Map<String, String> mappings;

    private Integer defaultMaxJobsActive = Integer.valueOf(-1);

    public ZeebeJobWorkerTypeCustomizer(ZeebeWorkerPropertiesProcessor propProc) {
        expander = propProc.getExpander();
        resolver = propProc.getResolver();
        Integer maxJobsActive = propProc.getProperties().getDefaultMaxJobsActive();
        if (maxJobsActive != null && 0 < maxJobsActive.intValue()) {
            defaultMaxJobsActive = propProc.getProperties().getDefaultMaxJobsActive();
            log.warn("Overwriting defaultMaxJobsActive value with " + defaultMaxJobsActive + ". Currently not used, caused a slow down!");
        }
    }

    @Override
    public void customize(ZeebeWorkerValue zeebeWorkerValue) {
        String currentType = zeebeWorkerValue.getType();
        if (currentType != null) {
            currentType = expander.expandTypeValue(currentType);
            currentType = resolver.resolve(currentType);
        }
        zeebeWorkerValue.setType(currentType);
//        if (defaultMaxJobsActive != null && 0 < defaultMaxJobsActive.intValue() &&
//                (zeebeWorkerValue.getMaxJobsActive() == null || zeebeWorkerValue.getMaxJobsActive().intValue() <= 0)) {
//            zeebeWorkerValue.setMaxJobsActive(defaultMaxJobsActive);
//        }
    }
}
