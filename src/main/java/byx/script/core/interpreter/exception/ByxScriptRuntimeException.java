package byx.script.core.interpreter.exception;

/**
 * 解释器运行时异常基类
 */
public class ByxScriptRuntimeException extends RuntimeException {
    public ByxScriptRuntimeException(String msg) {
        super(msg);
    }

    public ByxScriptRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
