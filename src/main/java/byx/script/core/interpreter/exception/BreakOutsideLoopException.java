package byx.script.core.interpreter.exception;

public class BreakOutsideLoopException extends ByxScriptRuntimeException {
    public BreakOutsideLoopException() {
        super("Break statement only allow in loop.");
    }
}
