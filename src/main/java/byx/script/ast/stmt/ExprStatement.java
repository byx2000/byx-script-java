package byx.script.ast.stmt;

import byx.script.ast.expr.Expr;
import byx.script.runtime.Scope;

public class ExprStatement implements Statement {
    private final Expr expr;

    public ExprStatement(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void execute(Scope scope) {
        expr.eval(scope);
    }
}