package com.camunda.consulting.zeebe.namespace.springboot.autoconfigure.test;

public class ZeebeWorkerStarterTestConstants {

    public static final String PROCESS_KEY = "processKey";

    public static final String VERSION_KEY = "version";

    public static final String PROCESS_KEY_PROC_ONE = "procOne";

    public static final String VERION_0_1_VALUE = "0_1";

    public static final String UNDERSCORE_DELIMITER = "_";

    public static final String JOB_WORKER_TYPE_PREFIX_WORKER_ONE = "_workerOne";

    public static final String JOB_WORKER_TYPE_POSTFIX_WORKER_ONE = "workerOne_";

    public static final String JOB_WORKER_TYPE_INFIX_WORKER_ONE = "_workerOne_";

    public static final String JOB_WORKER_TYPE_WORKER_ONE = "workerOne";

    public static final String EXPECTED_TYPE_PREFIXED = PROCESS_KEY_PROC_ONE + UNDERSCORE_DELIMITER + VERION_0_1_VALUE + JOB_WORKER_TYPE_PREFIX_WORKER_ONE;

    public static final String EXPECTED_TYPE_POSTFIX = JOB_WORKER_TYPE_POSTFIX_WORKER_ONE + PROCESS_KEY_PROC_ONE + UNDERSCORE_DELIMITER + VERION_0_1_VALUE;

    public static final String EXPECTED_TYPE_INFIX = PROCESS_KEY_PROC_ONE + UNDERSCORE_DELIMITER + VERION_0_1_VALUE + JOB_WORKER_TYPE_INFIX_WORKER_ONE + PROCESS_KEY_PROC_ONE + UNDERSCORE_DELIMITER + VERION_0_1_VALUE;

    public static final String VAR_KEY = "var";

    public static final String VAR_2_KEY = "var2";

    public static final String VAR_2S_KEY = "var2s";

    public static final String COUNT_KEY = "count";

    public static final String TAG_KEY = "tag";

    public static final String VAR_VALUE = "hello";

    public static final String VAR_2_VALUE = "hello_2";

    public static final String VAR_2S_VALUE = "hello_2s";

    public static final String COUNT_VALUE = "2";

    public static final String BEGIN_DATA = "begin";

    public static final String END_DATA = "end";

    public static final String TEST_DATA = "test";

    public static final String SINGLE_REF_RESULT = BEGIN_DATA + VAR_VALUE + END_DATA;

    public static final String DOUBLE_REF_RESULT = BEGIN_DATA + VAR_2_VALUE + END_DATA;

    public static final String DOUBLE_REF_COUNT_RESULT = BEGIN_DATA + VAR_2_VALUE + TEST_DATA + COUNT_VALUE + END_DATA;

    public static final String DOUBLE_S_REF_RESULT = BEGIN_DATA + VAR_2S_VALUE + END_DATA;

    public static final String DOUBLE_REF_OPEN_RESULT = BEGIN_DATA + VAR_2_VALUE;

    public static final String DOUBLE_S_REF_OPEN_RESULT = BEGIN_DATA + VAR_2S_VALUE;

    public static final String BEGIN_REF_RESULT = VAR_VALUE + END_DATA;

    public static final String END_REF_RESULT = BEGIN_DATA + VAR_VALUE;
}
