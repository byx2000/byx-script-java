package byx.script.parser.ast.stmt;

import byx.script.parser.ast.ASTVisitor;
import byx.script.parser.ast.expr.Expr;

/**
 * for语句
 * for (init; cond; update)
 *     body
 */
public class For implements Statement {
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
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
