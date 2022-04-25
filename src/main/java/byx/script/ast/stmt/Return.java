package byx.script.ast.stmt;

import byx.script.ast.ASTVisitor;
import byx.script.ast.expr.Expr;

/**
 * 函数返回语句
 */
public class Return implements Statement {
    private final Expr retVal;

    public Return(Expr retVal) {
        this.retVal = retVal;
    }

    public Expr getRetVal() {
        return retVal;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
