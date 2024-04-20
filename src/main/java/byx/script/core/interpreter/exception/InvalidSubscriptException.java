package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class InvalidSubscriptException extends ByxScriptRuntimeException {
    public InvalidSubscriptException(Value v) {
        super(String.format("invalid subscript value: %s", v));
    }
}
