package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public abstract class BinaryOp implements Expr {
    private final Expr lhs, rhs;

    protected BinaryOp(Expr lhs, Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    protected abstract Value doEval(Value v1, Value v2);

    @Override
    public Value eval(Scope scope) {
        return doEval(lhs.eval(scope), rhs.eval(scope));
    }
}
