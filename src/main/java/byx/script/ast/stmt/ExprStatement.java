package byx.script.ast.stmt;

import byx.script.ast.ASTVisitor;
import byx.script.ast.expr.Expr;

/**
 * 表达式语句
 * 对表达式求值，然后直接丢弃求值结果
 */
public class ExprStatement implements Statement {
    private final Expr expr;

    public ExprStatement(Expr expr) {
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(ctx, this);
    }
}
