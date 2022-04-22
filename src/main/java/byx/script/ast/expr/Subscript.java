package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

/**
 * 下标访问
 */
public class Subscript implements Expr {
    private final Expr expr;
    private final Expr subscript;

    public Subscript(Expr expr, Expr subscript) {
        this.expr = expr;
        this.subscript = subscript;
    }

    public Expr getExpr() {
        return expr;
    }

    public Expr getSubscript() {
        return subscript;
    }

    @Override
    public Value eval(Scope scope) {
        return expr.eval(scope).subscript(subscript.eval(scope));
    }
}
