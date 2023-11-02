package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure;

import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import io.camunda.zeebe.spring.client.configuration.ZeebeClientAllAutoConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

//@ConditionalOnMissingBean

@AutoConfiguration(before = ZeebeClientAllAutoConfiguration.class)
@ConditionalOnClass({ZeebeWorkerValueCustomizer.class, ZeebeWorkerValue.class})
@EnableConfigurationProperties({ZeebeWorkerProperties.class})
public class ZeebeWorkerAutoConfiguration {

    private Log log = LogFactory.getLog(ZeebeWorkerAutoConfiguration.class);

    public ZeebeWorkerAutoConfiguration() {
    }

    @Bean
    public ZeebeWorkerPropertiesProcessor workerPropertiesProcessor(ZeebeWorkerProperties props) {
        return new ZeebeWorkerPropertiesProcessor(props);
    }

    @Bean
    public ZeebeWorkerValueCustomizer workerTypeZeebeWorkerValueCustomizer(ZeebeWorkerPropertiesProcessor propProc) {
        return new ZeebeJobWorkerTypeCustomizer(propProc);
    }

    @Bean
    public ZeebeProcessVariableProvider processVariableProvider(ZeebeWorkerPropertiesProcessor propProc) {
        return propProc.getProcessVariableProvider();
    }
}
