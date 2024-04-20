package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class FieldAccessUnsupportedException extends ByxScriptRuntimeException {
    public FieldAccessUnsupportedException(Value v) {
        super(String.format("unsupported field access: %s", v.typeId()));
    }
}
