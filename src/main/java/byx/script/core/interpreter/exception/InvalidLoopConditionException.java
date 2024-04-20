package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class InvalidLoopConditionException extends ByxScriptRuntimeException {
    public InvalidLoopConditionException(Value v) {
        super(String.format("invalid loop condition: %s", v));
    }
}
