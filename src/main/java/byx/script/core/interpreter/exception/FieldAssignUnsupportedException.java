package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class FieldAssignUnsupportedException extends ByxScriptRuntimeException {
    public FieldAssignUnsupportedException(Value v) {
        super(String.format("unsupported field assign: %s", v.typeId()));
    }
}
