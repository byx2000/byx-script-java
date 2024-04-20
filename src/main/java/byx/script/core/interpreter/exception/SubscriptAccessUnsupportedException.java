package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class SubscriptAccessUnsupportedException extends ByxScriptRuntimeException {
    public SubscriptAccessUnsupportedException(Value v) {
        super(String.format("unsupported subscript access: %s", v.typeId()));
    }
}
