package byx.script.parser.ast.expr;

import byx.script.parser.ast.ASTVisitor;

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
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
