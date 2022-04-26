package byx.script.parser.ast.stmt;

import byx.script.parser.ast.ASTVisitor;
import byx.script.parser.ast.expr.Expr;
import byx.script.common.Pair;

import java.util.List;

/**
 * if语句
 * if (expr) stmt
 * else if (expr) stmt
 * ...
 * else stmt
 */
public class If implements Statement {
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
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
