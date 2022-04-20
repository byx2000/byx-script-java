package byx.script.runtime.control;

public class BreakException extends RuntimeException {
    public BreakException() {
        super(null, null, false, false);
    }
}
