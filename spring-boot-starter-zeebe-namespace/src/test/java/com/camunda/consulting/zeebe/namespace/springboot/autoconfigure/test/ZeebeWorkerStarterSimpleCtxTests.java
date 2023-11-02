package com.camunda.consulting.zeebe.namespace.springboot.autoconfigure.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.ZeebeWorkerPropertiesProcessor;

@SpringBootTest(classes = ZeebeWorkerStarterSimpleCtxTests.class)
@EnableAutoConfiguration
@ActiveProfiles("simple-test")
public class ZeebeWorkerStarterSimpleCtxTests {

    @Autowired
    private ZeebeWorkerPropertiesProcessor propProc;

    @Test
    public void testPropsAvailable() throws Exception {
        assertNotNull(propProc);
        assertNotNull(propProc.getProperties());
    }

    @Test
    public void testVariableStart() throws Exception {
        assertEquals(propProc.getProperties().getVariableStart(), "a${");
    }

    @Test
    public void testVariableEnd() throws Exception {
        assertEquals(propProc.getProperties().getVariableEnd(), "}a");
    }
}
