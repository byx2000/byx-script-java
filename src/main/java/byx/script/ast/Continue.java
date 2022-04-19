package byx.script.ast;

import byx.script.runtime.ContinueException;
import byx.script.runtime.Scope;

public class Continue implements Statement {
    private static final ContinueException CONTINUE_EXCEPTION = new ContinueException();

    @Override
    public void execute(Scope scope) {
        throw CONTINUE_EXCEPTION;
    }
}
