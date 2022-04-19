package byx.script.ast;

import byx.script.runtime.Scope;

public class IfElse implements Statement {
    private final Expr cond;
    private final Statement trueBranch;
    private final Statement falseBranch;

    public IfElse(Expr cond, Statement trueBranch, Statement falseBranch) {
        this.cond = cond;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public void execute(Scope scope) {
        if (cond.eval(scope).toCondition()) {
            trueBranch.execute(scope);
        } else {
            falseBranch.execute(scope);
        }
    }
}
