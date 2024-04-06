package byx.script.core.parser.ast.stmt;

import byx.script.core.interpreter.ASTVisitor;
import byx.script.core.parser.ast.expr.Expr;

/**
 * for语句
 * for (init; cond; update)
 *     body
 */
public class For extends Statement {
    private final Statement init;
    private final Expr cond;
    private final Statement update;
    private final Statement body;

    public For(Statement init, Expr cond, Statement update, Statement body) {
        this.init = init;
        this.cond = cond;
        this.update = update;
        this.body = body;
    }

    public Statement getInit() {
        return init;
    }

    public Expr getCond() {
        return cond;
    }

    public Statement getUpdate() {
        return update;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
