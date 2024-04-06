package byx.script.core.interpreter.exception;

/**
 * ByxScript执行的线程被中断时抛出此异常
 */
public class InterruptException extends ByxScriptRuntimeException {
    public InterruptException() {
        super("thread interrupted");
    }
}
