package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

public class FieldAccess implements Expr {
    private final Expr expr;
    private final String field;

    public FieldAccess(Expr expr, String field) {
        this.expr = expr;
        this.field = field;
    }

    @Override
    public Value eval(Scope scope) {
        return expr.eval(scope).getField(field);
    }

    @Override
    public void assign(Scope scope, Expr rhs) {
        expr.eval(scope).fieldAssign(field, rhs.eval(scope));
    }
}
