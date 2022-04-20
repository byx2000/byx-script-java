package byx.script.runtime.control;

import byx.script.runtime.Value;

public class ReturnException extends RuntimeException {
    private final Value retVal;

    public ReturnException(Value retVal) {
        super(null, null, false, false);
        this.retVal = retVal;
    }

    public Value getRetVal() {
        return retVal;
    }
}
