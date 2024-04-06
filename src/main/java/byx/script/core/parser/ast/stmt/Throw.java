package byx.script.core.parser.ast.stmt;

import byx.script.core.interpreter.ASTVisitor;
import byx.script.core.parser.ast.expr.Expr;

/**
 * throw语句
 */
public class Throw extends Statement {
    private final Expr expr;

    public Throw(Expr expr) {
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
