package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class UnaryOpException extends ByxScriptRuntimeException {
    public UnaryOpException(String op, Value v) {
        super(String.format("unsupported operator %s on %s", op, v.typeId()));
    }
}
