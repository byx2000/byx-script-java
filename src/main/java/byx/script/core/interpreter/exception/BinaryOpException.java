package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;

public class BinaryOpException extends ByxScriptRuntimeException {
    public BinaryOpException(String op, Value lhs, Value rhs) {
        super(String.format("unsupported operator %s between %s and %s", op, lhs.typeId(), rhs.typeId()));
    }
}
