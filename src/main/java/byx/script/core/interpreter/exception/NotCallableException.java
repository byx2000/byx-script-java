package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class NotCallableException extends ByxScriptRuntimeException {
    public NotCallableException(Value v) {
        super(String.format("%s is not callable", v.typeId()));
    }
}
