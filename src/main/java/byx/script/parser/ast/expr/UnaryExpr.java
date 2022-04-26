package byx.script.parser.ast.expr;

import byx.script.parser.ast.ASTVisitor;

/**
 * 一元表达式
 */
public class UnaryExpr implements Expr {
    private final UnaryOp op;
    private final Expr e;

    public UnaryExpr(UnaryOp op, Expr e) {
        this.op = op;
        this.e = e;
    }

    public UnaryOp getOp() {
        return op;
    }

    public Expr getExpr() {
        return e;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
