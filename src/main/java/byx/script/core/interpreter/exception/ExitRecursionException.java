package byx.script.core.interpreter.exception;

import byx.script.core.common.FastException;

public class ExitRecursionException extends FastException {
    private final Runnable runnable;

    public ExitRecursionException(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
