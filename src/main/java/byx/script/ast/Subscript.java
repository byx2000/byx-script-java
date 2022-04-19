package byx.script.ast;

import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public class Subscript implements Expr {
    private final Expr expr;
    private final Expr subscript;

    public Subscript(Expr expr, Expr subscript) {
        this.expr = expr;
        this.subscript = subscript;
    }

    @Override
    public Value eval(Scope scope) {
        return expr.eval(scope).subscript(subscript.eval(scope));
    }

    @Override
    public void assign(Scope scope, Expr rhs) {
        expr.eval(scope).subscriptAssign(subscript.eval(scope), rhs.eval(scope));
    }
}
