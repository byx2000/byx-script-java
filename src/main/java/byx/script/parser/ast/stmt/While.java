package byx.script.parser.ast.stmt;

import byx.script.parser.ast.ASTVisitor;
import byx.script.parser.ast.expr.Expr;

/**
 * while语句
 * while (cond)
 *     body
 */
public class While implements Statement {
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
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
