package byx.script.core.parser.ast.expr;

import byx.script.core.interpreter.ASTVisitor;

/**
 * 一元表达式
 */
public class UnaryExpr extends Expr {
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
    protected  <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
