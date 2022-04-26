package byx.script.interpreter;

import byx.script.common.FastException;
import byx.script.interpreter.value.Value;

/**
 * 执行return语句时会抛出该异常，并传递返回值
 */
public class ReturnException extends FastException {
    private final Value retVal;

    public ReturnException(Value retVal) {
        this.retVal = retVal;
    }

    public Value getRetVal() {
        return retVal;
    }
}
