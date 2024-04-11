package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;
import byx.script.core.common.FastException;

public class BuiltinFunctionException extends FastException {
    private final Value value;

    public BuiltinFunctionException(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
