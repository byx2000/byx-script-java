package byx.script.core.parser.ast.expr;

import byx.script.core.interpreter.ASTVisitor;

import java.util.List;

/**
 * 函数调用
 */
public class Call extends Expr {
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
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
