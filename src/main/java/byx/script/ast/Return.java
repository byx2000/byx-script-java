package byx.script.ast;

import byx.script.runtime.ReturnException;
import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public class Return implements Statement {
    private final Expr retVal;

    public Return(Expr retVal) {
        this.retVal = retVal;
    }

    @Override
    public void execute(Scope scope) {
        if (retVal != null) {
            throw new ReturnException(retVal.eval(scope));
        }
        throw new ReturnException(Value.UNDEFINED);
    }
}
