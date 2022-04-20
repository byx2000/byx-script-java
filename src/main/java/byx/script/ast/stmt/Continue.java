package byx.script.ast.stmt;

import byx.script.ast.Statement;
import byx.script.runtime.control.ContinueException;
import byx.script.runtime.Scope;

public class Continue implements Statement {
    private static final ContinueException CONTINUE_EXCEPTION = new ContinueException();

    @Override
    public void execute(Scope scope) {
        throw CONTINUE_EXCEPTION;
    }
}
