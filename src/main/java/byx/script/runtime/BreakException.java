package byx.script.runtime;

public class BreakException extends RuntimeException {
    public BreakException() {
        super(null, null, false, false);
    }
}
