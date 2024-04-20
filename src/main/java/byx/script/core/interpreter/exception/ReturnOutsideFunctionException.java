package byx.script.core.interpreter.exception;

public class ReturnOutsideFunctionException extends ByxScriptRuntimeException {
    public ReturnOutsideFunctionException() {
        super("Return statement only allow in function.");
    }
}
