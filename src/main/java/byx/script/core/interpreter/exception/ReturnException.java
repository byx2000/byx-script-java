package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;
import byx.script.core.common.FastException;

public class ReturnException extends FastException {
    private final Value retVal;

    public ReturnException(Value retVal) {
        this.retVal = retVal;
    }

    public Value getRetVal() {
        return retVal;
    }
}
