package byx.script.core.parser.ast.expr;

import byx.script.core.interpreter.ASTVisitor;

/**
 * 二元表达式
 */
public class BinaryExpr extends Expr {
    private final BinaryOp op;
    private final Expr lhs, rhs;

    public BinaryExpr(BinaryOp op, Expr lhs, Expr rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public BinaryOp getOp() {
        return op;
    }

    public Expr getLhs() {
        return lhs;
    }

    public Expr getRhs() {
        return rhs;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
