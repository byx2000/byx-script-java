package byx.script.core.parser.ast.stmt;

import byx.script.core.interpreter.ASTVisitor;
import byx.script.core.parser.ast.expr.Expr;

/**
 * while语句
 * while (cond)
 *     body
 */
public class While extends Statement {
    private final Expr cond;
    private final Statement body;

    public While(Expr cond, Statement body) {
        this.cond = cond;
        this.body = body;
    }

    public Expr getCond() {
        return cond;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
