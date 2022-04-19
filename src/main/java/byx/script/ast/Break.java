package byx.script.ast;

import byx.script.runtime.BreakException;
import byx.script.runtime.Scope;

public class Break implements Statement {
    private static final BreakException BREAK_EXCEPTION = new BreakException();

    @Override
    public void execute(Scope scope) {
        throw BREAK_EXCEPTION;
    }
}
