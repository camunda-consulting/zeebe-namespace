package com.camunda.consulting.zeebe.namespace.springboot.autoconfigure.test;

import io.camunda.zeebe.spring.client.annotation.customizer.ZeebeWorkerValueCustomizer;
import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.ZeebeJobWorkerTypeCustomizer;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.ZeebeWorkerPropertiesProcessor;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.ZeebeWorkerTypeExpander;

@SpringBootTest(classes = ZeebeWorkerStarterVarCtxTests.class)
@EnableAutoConfiguration
@ActiveProfiles("var-test")
public class ZeebeWorkerStarterVarCtxTests {

    @Autowired
    private ZeebeWorkerPropertiesProcessor propProc;

    @Autowired
    private ZeebeJobWorkerTypeCustomizer customizer;

    @Test
    public void testResolvedFinalTag() throws Exception {
        ZeebeWorkerTypeExpander expander = propProc.getExpander();
        assertEquals(expander.getResolvedTagValue(), "var_test");
    }

    @Test
    public void testResolvedPrefix() throws Exception {
        ZeebeWorkerTypeExpander expander = propProc.getExpander();
        assertEquals(expander.expandTypeValue("_task"), "var_test_task");
    }

    @Test
    public void testResolvedPostfix() throws Exception {
        ZeebeWorkerTypeExpander expander = propProc.getExpander();
        assertEquals(expander.expandTypeValue("task_"), "task_var_test");

    }

    @Test
    public void testResolvedVarRef() throws Exception {
        checkWorker("_task${middleValue}", "var_test_taskhello");
    }

    private void checkWorker(String inType, String resultType) throws Exception {
        ZeebeWorkerValue inWorker = createWorker(inType);
        ZeebeWorkerValue rcWorker = createWorker(resultType);
        customizer.customize(inWorker);
        assertEquals(inWorker.getType(), rcWorker.getType());
    }

    private ZeebeWorkerValue createWorker(String type) {
        ZeebeWorkerValue rcWorker = new ZeebeWorkerValue();
        rcWorker.setType(type);
        return rcWorker;
    }
}
