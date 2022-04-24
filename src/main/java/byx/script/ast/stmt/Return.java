package byx.script.ast.stmt;

import byx.script.ast.expr.Expr;
import byx.script.runtime.Scope;
import byx.script.runtime.control.ReturnException;
import byx.script.runtime.value.Value;

/**
 * 函数返回语句
 */
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
        throw new ReturnException(Value.undefined());
    }
}
