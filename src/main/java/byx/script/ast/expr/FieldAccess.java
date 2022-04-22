package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

/**
 * 字段访问
 */
public class FieldAccess implements Expr {
    private final Expr expr;
    private final String field;

    public FieldAccess(Expr expr, String field) {
        this.expr = expr;
        this.field = field;
    }

    public Expr getExpr() {
        return expr;
    }

    public String getField() {
        return field;
    }

    @Override
    public Value eval(Scope scope) {
        return expr.eval(scope).getField(field);
    }
}
