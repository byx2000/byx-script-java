package byx.script.ast.expr;

import byx.script.ast.ASTVisitor;

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
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
