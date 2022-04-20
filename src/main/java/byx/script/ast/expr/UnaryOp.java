package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public abstract class UnaryOp implements Expr {
    private final Expr expr;

    protected UnaryOp(Expr expr) {
        this.expr = expr;
    }

    protected abstract Value doEval(Value v);

    @Override
    public Value eval(Scope scope) {
        return doEval(expr.eval(scope));
    }
}
