package byx.script.core.parser.ast.stmt;

import byx.script.core.interpreter.ASTVisitor;
import byx.script.core.parser.ast.expr.Expr;

/**
 * 函数返回语句
 */
public class Return extends Statement {
    private final Expr retVal;

    public Return(Expr retVal) {
        this.retVal = retVal;
    }

    public Expr getRetVal() {
        return retVal;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
