package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class SubscriptAssignUnsupportedException extends ByxScriptRuntimeException {
    public SubscriptAssignUnsupportedException(Value v) {
        super(String.format("unsupported subscript assign: %s", v.typeId()));
    }
}
