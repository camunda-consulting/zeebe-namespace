package org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import org.camunda.consulting.zeebe.namespace.springboot.autoconfigure.ZeebeWorkerTypeModeEnum;

public class ZeebeWorkerTypeResolver {

    public enum State {
        TEXT,
        VAR
    }

    public class ResolveCtx {

        public State state;

        public ResolveCtx() {
            state = State.TEXT;
        }
    }

    public class ResolveFrame {
        public String varPart;

        public ResolveFrame(String varPart) {
            this.varPart = varPart;
        }
    }

    private String variableStart;

    private String variableEnd;

    private Map<String, String> variableValues;

    private Function<String, String> resolveFunc;

    public ZeebeWorkerTypeResolver(ZeebeWorkerTypeModeEnum mode, String start, String end) {
        variableStart = start;
        variableEnd = end;
        if (mode.equals(ZeebeWorkerTypeModeEnum.OFF)
                || variableStart == null || variableStart.length() <= 0
                || variableEnd == null || variableEnd.length() <= 0) {
            resolveFunc = this::noResolve;
        } else {
            resolveFunc = this::internalResolve;
        }
    }

    public void setVariableStart(String variableStart) {
        this.variableStart = variableStart;
    }

    public void setVariableEnd(String variableEnd) {
        this.variableEnd = variableEnd;
    }

    public void setVariableValues(Map<String, String> variableValues) {
        this.variableValues = new HashMap<>(variableValues);
    }

    public String resolve(String pattern) {
        return resolveFunc.apply(pattern);
    }

    protected String noResolve(String pattern) {
        return pattern;
    }

    protected String internalResolve(String pattern) {
        Stack<ResolveFrame> stack = new Stack<>();
        StringBuilder result = new StringBuilder();
        String varName = "";
        boolean goOn = true;
        ResolveCtx ctx = new ResolveCtx();
        int leftIdx = 0;
        int varStartIdx = -1;
        int varEndIdx = -1;
        while (goOn) {
            goOn = switch(ctx.state) {
                case TEXT:
                    varStartIdx = pattern.indexOf(variableStart, leftIdx);
                    if (varStartIdx < 0) {
                        result.append(pattern.substring(leftIdx));
                        yield false;
                    } else {
                        result.append(pattern.substring(leftIdx, varStartIdx));
                        leftIdx = varStartIdx + variableStart.length();
                        ctx.state = State.VAR;
                        yield true;
                    }
                case VAR:
                    varStartIdx = pattern.indexOf(variableStart, leftIdx);
                    varEndIdx = pattern.indexOf(variableEnd, leftIdx);
                    // We are in an open variable references
                    // => if we haven't found an end marker, we set an implicit marker at the end
                    if (varEndIdx < 0) varEndIdx = pattern.length();
                    if (varStartIdx < 0 || varEndIdx <= varStartIdx) {
                        // No next variable start marker found
                        // or the next marker is a closing var reference
                        // The "<=" is important, if start- and end-marker is the same, e.g. "@"
                        varName = varName + pattern.substring(leftIdx, varEndIdx);
                        String varValue = variableValues.getOrDefault(varName, "");
                        leftIdx = varEndIdx + variableEnd.length();
                        if (stack.isEmpty()) {
                            result.append(varValue);
                            varName = "";
                            ctx.state = State.TEXT;
                        } else {
                            varName = stack.pop().varPart + varValue;
                            // The following if triggers only at the end of pattern,
                            // if we have not enough closing refs.
                            if (pattern.length() <= leftIdx) leftIdx = pattern.length();
                        }
                    } else {
                         // Next is an additional start reference
                        varName = pattern.substring(leftIdx, varStartIdx);
                        stack.push(new ResolveFrame(varName));
                        varName = "";
                        leftIdx = varStartIdx + variableStart.length();
                    }
                    yield (leftIdx < pattern.length()) || (0 < varName.length());
                    //yield leftIdx < pattern.length();
                default:
                    yield false;
            };
        }
        return result.toString();
    }
}
