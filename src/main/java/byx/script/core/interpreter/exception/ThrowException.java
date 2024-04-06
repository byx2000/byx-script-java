package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;
import byx.script.core.common.FastException;

public class ThrowException extends FastException {
    private final Value value;

    public ThrowException(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
