package byx.script.core.parser.ast.expr;

import byx.script.core.interpreter.ASTVisitor;

/**
 * 下标访问
 */
public class Subscript extends Expr {
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
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
