package byx.script.parser.ast.stmt;

import byx.script.parser.ast.ASTVisitor;
import byx.script.parser.ast.expr.Expr;

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
        return visitor.visit(this, ctx);
    }
}
