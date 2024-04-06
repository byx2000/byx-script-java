package byx.script.core.parser.ast.stmt;

import byx.script.core.interpreter.ASTVisitor;
import byx.script.core.parser.ast.expr.Expr;

/**
 * 表达式语句
 * 对表达式求值，然后直接丢弃求值结果
 */
public class ExprStatement extends Statement {
    private final Expr expr;

    public ExprStatement(Expr expr) {
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
