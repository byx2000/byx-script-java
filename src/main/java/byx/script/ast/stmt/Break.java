package byx.script.ast.stmt;

import byx.script.ast.Statement;
import byx.script.runtime.control.BreakException;
import byx.script.runtime.Scope;

public class Break implements Statement {
    private static final BreakException BREAK_EXCEPTION = new BreakException();

    @Override
    public void execute(Scope scope) {
        throw BREAK_EXCEPTION;
    }
}
