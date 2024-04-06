package byx.script.core.parser.ast.stmt;

import byx.script.core.common.Pair;
import byx.script.core.interpreter.ASTVisitor;
import byx.script.core.parser.ast.expr.Expr;

import java.util.List;

/**
 * if语句
 * if (expr) stmt
 * else if (expr) stmt
 * ...
 * else stmt
 */
public class If extends Statement {
    private final List<Pair<Expr, Statement>> cases;
    private final Statement elseBranch;

    public If(List<Pair<Expr, Statement>> cases, Statement elseBranch) {
        this.cases = cases;
        this.elseBranch = elseBranch;
    }

    public List<Pair<Expr, Statement>> getCases() {
        return cases;
    }

    public Statement getElseBranch() {
        return elseBranch;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
