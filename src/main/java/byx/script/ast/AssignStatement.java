package byx.script.ast;

import byx.script.runtime.Scope;

public class AssignStatement implements Statement {
    private final Expr lhs, rhs;

    public AssignStatement(Expr lhs, Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void execute(Scope scope) {
        lhs.assign(scope, rhs);
    }
}
