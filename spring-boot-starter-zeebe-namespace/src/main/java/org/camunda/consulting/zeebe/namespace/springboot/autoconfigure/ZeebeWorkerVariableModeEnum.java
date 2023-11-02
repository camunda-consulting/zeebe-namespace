package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure;

/**
 * Selects how many variables should be available in map.
 */
public enum ZeebeWorkerVariableModeEnum {

    /**
     * Copy all variables from property mappings to map.
     */
    ALL,

    /**
     * Copy the configured tagName and all in the construction of this tagName used variables
     */
    FULLTAG,

    /**
     * Copy only the generated tagName to the map
     */
    TAGONLY,

    OFF
}
