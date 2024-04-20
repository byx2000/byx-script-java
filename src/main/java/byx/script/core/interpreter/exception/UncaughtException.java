package byx.script.core.interpreter.exception;

import byx.script.core.interpreter.value.Value;
import byx.script.core.util.ValueUtils;

public class UncaughtException extends ByxScriptRuntimeException {
    public UncaughtException(Value value) {
        super("Uncaught exception from script: " + ValueUtils.valueToString(value));
    }
}
