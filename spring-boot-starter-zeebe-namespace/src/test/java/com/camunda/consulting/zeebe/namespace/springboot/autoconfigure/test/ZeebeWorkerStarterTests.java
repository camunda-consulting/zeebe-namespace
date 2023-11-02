package com.camunda.consulting.zeebe.namespace.springboot.autoconfigure.test;

import io.camunda.zeebe.spring.client.annotation.value.ZeebeWorkerValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static com.camunda.consulting.zeebe.namespace.springboot.autoconfigure.test.ZeebeWorkerStarterTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.*;

public class ZeebeWorkerStarterTests {

    private ZeebeWorkerProperties props;

    private ZeebeWorkerPropertiesProcessor propsProc;

    private ZeebeJobWorkerTypeCustomizer customizer;

    @BeforeEach
    public void initHelper() {
        props = new ZeebeWorkerProperties();
        // Setup props
        props.getMappings().put(ZeebeWorkerStarterTestConstants.PROCESS_KEY, ZeebeWorkerStarterTestConstants.PROCESS_KEY_PROC_ONE);
        props.getMappings().put(ZeebeWorkerStarterTestConstants.VERSION_KEY, ZeebeWorkerStarterTestConstants.VERION_0_1_VALUE);
    }

    protected void createCustomizer() {
        this.propsProc = new ZeebeWorkerPropertiesProcessor(props);
        this.customizer = new ZeebeJobWorkerTypeCustomizer(propsProc);
    }

    protected void checkWorkerType(String typeValue, String expectedResult) {
        ZeebeWorkerValue wv = new ZeebeWorkerValue();
        wv.setType(typeValue);
        ZeebeWorkerValue expWv = new ZeebeWorkerValue();
        expWv.setType(expectedResult);
        customizer.customize(wv);
        assertEquals(expWv.getType(), wv.getType());
    }

    protected void enhancePropsForVarTest() {
        props.getMappings().put(VAR_KEY, VAR_VALUE);
        props.getMappings().put(VAR_2_KEY, VAR_2_VALUE);
        props.getMappings().put(VAR_2S_KEY, VAR_2S_VALUE);
        props.getMappings().put(COUNT_KEY, COUNT_VALUE);
        props.setTypeMode(ZeebeWorkerTypeModeEnum.EXPLICIT);
    }

    @Test
    public void testTypeValue() throws Exception {
        createCustomizer();
        checkWorkerType(JOB_WORKER_TYPE_WORKER_ONE, JOB_WORKER_TYPE_WORKER_ONE);
    }

    @Test
    public void testPrefixTypeValue() throws Exception {
        createCustomizer();
        checkWorkerType(JOB_WORKER_TYPE_PREFIX_WORKER_ONE, EXPECTED_TYPE_PREFIXED);
    }

    @Test
    public void testPostfixTypeValue() throws Exception {
        createCustomizer();
        checkWorkerType(JOB_WORKER_TYPE_POSTFIX_WORKER_ONE, EXPECTED_TYPE_POSTFIX);
    }

    @Test
    public void testInfixTypeValue() throws Exception {
        createCustomizer();
        checkWorkerType(JOB_WORKER_TYPE_INFIX_WORKER_ONE, EXPECTED_TYPE_INFIX);
    }

    @Test
    public void testPrefixOnlyTypeValue() throws Exception {
        props.setTypeMode(ZeebeWorkerTypeModeEnum.PREFIX);
        createCustomizer();
        checkWorkerType(JOB_WORKER_TYPE_WORKER_ONE, EXPECTED_TYPE_PREFIXED);
    }

    @Test
    public void testPostfixOnlyTypeValue() throws Exception {
        props.setTypeMode(ZeebeWorkerTypeModeEnum.POSTFIX);
        createCustomizer();
        checkWorkerType(JOB_WORKER_TYPE_WORKER_ONE, EXPECTED_TYPE_POSTFIX);
    }

    @Test
    public void testAutoUnknwonRef() throws Exception {
        props.setTypeMode(ZeebeWorkerTypeModeEnum.AUTO);
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${unknown_var}" + END_DATA, BEGIN_DATA + END_DATA);
    }

    @Test
    public void testSingleRef() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${" + VAR_KEY + "}" + END_DATA, SINGLE_REF_RESULT);
    }

    @Test
    public void testDoubleRef() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${" + VAR_KEY + "${" + COUNT_KEY + "}}" + END_DATA, DOUBLE_REF_RESULT);
    }

    @Test
    public void testDoubleRefAndCount() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${" + VAR_KEY + "${" + COUNT_KEY + "}}" + TEST_DATA + "${" + COUNT_KEY + "}" + END_DATA, DOUBLE_REF_COUNT_RESULT);
    }

    @Test
    public void testDoubleSRef() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${" + VAR_KEY + "${" + COUNT_KEY + "}s}" + END_DATA, DOUBLE_S_REF_RESULT);
    }

    @Test
    public void testBeginRef() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType("${" + VAR_KEY + "}" + END_DATA, BEGIN_REF_RESULT);
    }

    @Test
    public void testEndRef() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${" + VAR_KEY + "}", END_REF_RESULT);
    }

    @Test
    public void testEndRefOpenEnd() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${" + VAR_KEY, END_REF_RESULT);
    }

    @Test
    public void testDoubleEndRefOpenEnd() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${" + VAR_KEY + "${" + COUNT_KEY, DOUBLE_REF_OPEN_RESULT);
    }

    @Test
    public void testDoubleEndSRefOpenEnd() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${" + VAR_KEY + "${" + COUNT_KEY +"}s", DOUBLE_S_REF_OPEN_RESULT);
    }

    @Test
    public void testExplicitUnknwonRef() throws Exception {
        enhancePropsForVarTest();
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "${unknown_var}" + END_DATA, BEGIN_DATA + END_DATA);
    }

    @Test
    public void testSingleDelim() throws Exception {
        enhancePropsForVarTest();
        props.setVariableStart("@");
        props.setVariableEnd("@");
        createCustomizer();
        checkWorkerType(BEGIN_DATA + "@" + VAR_KEY + "@" + END_DATA, SINGLE_REF_RESULT);
    }

    @Test
    public void testResolvedPrefixedTag() throws Exception {
        props.setTypeMode(ZeebeWorkerTypeModeEnum.PREFIX);
        createCustomizer();
        String resolvedTag = propsProc.getExpander().getResolvedTagValue();
        String expectedResolvedTag = (String)propsProc.getProcessVariableProvider().baseVariables().get(props.getTagName());
        assertEquals(resolvedTag + props.getTypeDelimiter(), expectedResolvedTag);
    }

    @Test
    public void testResolvedPostfixTag() throws Exception {
        props.setTypeMode(ZeebeWorkerTypeModeEnum.POSTFIX);
        createCustomizer();
        String resolvedTag = propsProc.getExpander().getResolvedTagValue();
        String expectedResolvedTag = (String)propsProc.getProcessVariableProvider().baseVariables().get(props.getTagName());
        assertEquals(props.getTypeDelimiter() + resolvedTag, expectedResolvedTag);
    }

    @Test
    public void testResolvedAutoTag() throws Exception {
        props.setTypeMode(ZeebeWorkerTypeModeEnum.AUTO);
        createCustomizer();
        String resolvedTag = propsProc.getExpander().getResolvedTagValue();
        String expectedResolvedTag = (String)propsProc.getProcessVariableProvider().baseVariables().get(props.getTagName());
        assertEquals(resolvedTag, expectedResolvedTag);
    }

    @Test
    public void testResolvedTagAlleDfault() throws Exception {
        createCustomizer();
        String resolvedTag = propsProc.getExpander().getResolvedTagValue();
        String expectedResolvedTag = (String)propsProc.getProcessVariableProvider().baseVariables().get(props.getTagName());
        assertEquals(resolvedTag, expectedResolvedTag);
    }

    @Test
    public void testResolvedUntouchedTag() throws Exception {
        props.setTypeMode(ZeebeWorkerTypeModeEnum.PREFIX);
        props.setTagValueDelimiterMode(ZeebeWorkerTagValueDelimiterMode.UNTOUCHED);
        createCustomizer();
        String resolvedTag = propsProc.getExpander().getResolvedTagValue();
        String expectedResolvedTag = (String)propsProc.getProcessVariableProvider().baseVariables().get(props.getTagName());
        assertEquals(resolvedTag, expectedResolvedTag);
    }

//    @Test
//    public void testUnsetMaxJobs() throws Exception {
//        props.setDefaultMaxJobsActive(Integer.valueOf(5));
//        createCustomizer();
//        ZeebeWorkerValue wv = new ZeebeWorkerValue();
//        wv.setType(BEGIN_DATA);
//        customizer.customize(wv);
//        assertEquals(wv.getMaxJobsActive(), Integer.valueOf(5));
//    }
//
//    @Test
//    public void testDefinedMaxJobs() throws Exception {
//        props.setDefaultMaxJobsActive(Integer.valueOf(5));
//        createCustomizer();
//        ZeebeWorkerValue wv = new ZeebeWorkerValue();
//        wv.setType(BEGIN_DATA);
//        wv.setMaxJobsActive(Integer.valueOf(10));
//        customizer.customize(wv);
//        assertEquals(wv.getMaxJobsActive(), Integer.valueOf(10));
//    }
}
