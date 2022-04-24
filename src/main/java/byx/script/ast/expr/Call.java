package byx.script.ast.expr;

import byx.script.ast.ASTVisitor;

import java.util.List;

/**
 * 函数调用
 */
public class Call implements Expr {
    private final Expr expr;
    private final List<Expr> args;

    public Call(Expr callable, List<Expr> expr) {
        this.expr = callable;
        this.args = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    public List<Expr> getArgs() {
        return args;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(ctx, this);
    }
}
