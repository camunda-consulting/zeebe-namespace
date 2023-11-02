# Namespaces for Zeebe (Camunda 8) Applications

<!--
In the general process modeling and worker implementation you have to take care,
that your task type name doesn't get used more than once on all your processes.

If you apply this to a single team, it may work out by communication between the
team members.
-->

If you apply process orchestration with Camunda over a huge company, it is hard
to avoid name clashes of task types and called processes. Several teams may use
the same name for different task types and/or worker implementations or in call
activities.

Namespaces for each project helps you here.

This project offer some configuration mechanisms to set the namespace in a
single place and apply it to all process models and workers used in this spring
boot project.

## Adding pre- and postfix to process ID

As there is no hook in the Zeebe client to change the BPMN diagram before
deploying, you can use the Maven-Resources-Plugin to adjust the process model
with a namespace.

The process ID has to be a valid XML ID, so the characters "@" and "${}" are not
allowed here.

But you can define another delimiter like "AAA" to change the process ID with
Maven before is gets packed into the war files.

The process ID contains the value `AAAprocess.tagAAA_deliveryProcess`. The Maven
resources plugin evaluates it to `processOne_1_0_deliveryProcess`. The following
configuration chain is required to change the process ID.

### Maven-Resources-Plugin configuration

```
<plugin>
  <artifactId>maven-resources-plugin</artifactId>
  <configuration>
    <delimiters>
      <delimiter>${*}</delimiter>
      <delimiter>@</delimiter>
      <delimiter>AAA</delimiter>
    </delimiters>
  </configuration>
</plugin>
```

Don't forget to
[enable filtering](https://maven.apache.org/plugins/maven-resources-plugin/examples/filter.html)
to replace the variables with their values.

### pom.xml properties

```
<properties>
  <process.key>processOne</process.key>
  <process.version>1_0</process.version>
  <process.tag>${process.key}_${process.version}</process.tag>
</properties>
```

## Adding pre- and postfix to task types

Without any configuration, as task type `_singleWork` is picked up by a worker
with the annotation `@JobWorker(type = "_myTaskType")`.

The connection between task type and worker annotation can be configured using
an ID tag. The value will be put in front or appended to the worker type. And
the variables for ID tag value are injected when a new process instance is
started.

As an example put the following values into the `application.properties`
configuration

```
zeebe.namespace.mappings.processKey=processOne
zeebe.namespace.mappings.version=1_0
```

Now the worker from above registers to tasks with type
`processOne_1_0_myTaskType`.

To create a task with the type, the type field in the modeler requires a FEEL
expression: `= pt + "_singleWork"`. `pt` is the default value for the
`zeebe.namespace.conf.tagName` configuration.

The value for the variable `pt` is set automatically by the starter.

## Adding pre- and postfix to call activities

To create the called process, you have to apply the same tasks as for the
processes.

To call the processes from the super processes, add a FEEL expression for the
Process ID of the called element like `pt + "subProcessOne`.

Given the same value for `pt` as above, the expression gets evaluated to
`processOne_1_0_subProcessOne`.

## Complete configuration properties

### ID tag

The ID tag gets calculated connecting the variables listed in
`zeebe.namespace.variableNames` and `zeebe.namespace.typeDelimiter`, configured
by the `zeebe.namespace.tagValueDelimiterMode`. It takes the values for the
variables from `zeebe.namespace.mappings.<key>`.

| `tagValueDelimiterMode` | Explanation                                                                                                                                                     | value for `pt`   |
| ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------- |
| AS_PREFIX               | Adds the delimiter to the end of the ID tag. The task type can be given without a FEEL expression                                                               | processOne_1_0\_ |
| AS_POSTFIX              | Adds the delimiter in front of the ID tag. The task type can be given without a FEEL expression                                                                 | \_processOne_1_0 |
| DEPENDS_TYPE_MODE       | If `typeMode` is set to AUTO, EXPLICIT or OFF, no delimiter will be added to the ID tag. If `typeMode` is set to PREFIX or POSTFIX, see AS_PREFIX or AS_POSTFIX | See other values |
| UNTOUCHED               | The delimiter will not be added                                                                                                                                 | processOne_1_0   |

### Type mode

The `zeebe.namespace.typeMode` configuration influences how the `@JobWorker`
type annotation is processed and registered.

These values are defined:

- **AUTO**: it checks if the type value begins and/or ends with the value of the
  `typeDelimiter`. At each position, the value of the ID tag will be inserted.
  Examples:

  | type value    | result                                   |
  | ------------- | ---------------------------------------- |
  | \_singleWork  | processOne_1_0_singleWork                |
  | singleWork\_  | singleWork_processOne_1_0                |
  | \_singleOne\_ | processOne_1_0_singleWork_processOne_1_0 |

- **PREFIX**: the ID tag will always be inserted in front of the type, connected
  with the value of the `typeDelimiter`. The delimiter must not be given in the
  process definition.

  | type value | result                    |
  | ---------- | ------------------------- |
  | singleWork | processOne_1_0_singleWork |

- **POSTFIX**: The ID tag will always be appended to the type, connected with
  the value of the `typeDelimiter`. The delimiter must not be given in the
  process definition.

  | type value | result                    |
  | ---------- | ------------------------- |
  | singleWork | singleWork_processOne_1_0 |

- **EXPLICIT**: No ID tag will be calculated, only the referenced variables will
  be replaced. Given the configuration `zeebe.namespace.mappings.test=done`.

  | type value          | result          |
  | ------------------- | --------------- |
  | singleWork\_${test} | singleWork_done |

- **OFF**: The type values will be used without any replacement. It may help
  with debugging the setup, as all replacement will the logged on startup.

### Configuration reference

| Property                                | Range of values                                     | Description                                                                      | Default value       |
| --------------------------------------- | --------------------------------------------------- | -------------------------------------------------------------------------------- | ------------------- |
| `zeebe.namespace.mappings.<key>`        | String                                              | Creates a variable in the list with name `<key>` and value.                      |                     |
| `zeebe.namespace.tagName`               | String                                              | Gets calculated from `tagVariableNames` and `typeDelimiter` to create the ID-tag | pt                  |
| `zeebe.namespace.tagValueDelimiterMode` | AS_PREFIX, AS_POSTFIX, DEPENDS_TYPE_MODE, UNTOUCHED | Where should the value of `tagDelimiter` be placed                               | DEPENDS_TYPE_MODE   |
| `zeebe.namespace.tagVariableNames`      | String list                                         | Comma delimited list of variable names to create the ID-tag with `typeDelimiter` | processKey, version |
| `zeebe.namespace.typeDelimiter`         | String                                              | The delimiter between variables                                                  | \_ (underscore)     |
| `zeebe.namespace.typeMode`              | AUTO, PREFIX, POSTFIX, EXPLICIT, OFF                | See below                                                                        | AUTO                |
| `zeebe.namespace.variableMode`          | ALL, FULLTAG, TAGONLY, OFF                          | Defines to inject variables into started process instances. More details below.  | ALL                 |
| `zeebe.namespace.variableStart`         | String                                              | Begin of a variable reference in the `type` field                                | ${                  |
| `zeebe.namespace.variableEnd`           | String                                              | End of a variables reference in the `type` field                                 | }                   |

## Examples

See the subfolder [examples](/examples/) for executable examples.

## Credentials

The code of this project comes from Bernd Köcke from [1&1](https://1und1.de).
