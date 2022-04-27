package byx.script.interpreter;

import byx.script.common.FastException;
import byx.script.interpreter.value.Value;

/**
 * 执行throw语句时会抛出该异常
 * 外层的Try节点捕获该异常后执行相关操作
 */
public class ThrowException extends FastException {
    private final Value value;

    public ThrowException(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
