package byx.script.core.interpreter.exception;

public class ContinueOutsideLoopException extends ByxScriptRuntimeException {
    public ContinueOutsideLoopException() {
        super("Continue statement only allow in loop.");
    }
}
