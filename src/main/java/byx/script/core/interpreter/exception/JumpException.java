package byx.script.core.interpreter.exception;

import byx.script.core.common.FastException;

public class JumpException extends FastException {
    private final Runnable runnable;

    public JumpException(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
