package byx.script.ast.stmt;

import byx.script.ast.Statement;
import byx.script.ast.Expr;
import byx.script.runtime.value.BoolValue;
import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public class IfElse implements Statement {
    private final Expr cond;
    private final Statement trueBranch;
    private final Statement falseBranch;

    public IfElse(Expr cond, Statement trueBranch, Statement falseBranch) {
        this.cond = cond;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    private boolean getCondition(Value v) {
        if (v instanceof BoolValue) {
            return ((BoolValue) v).getValue();
        }
        throw new InterpretException("condition of if statement must be bool value");
    }

    @Override
    public void execute(Scope scope) {
        if (getCondition(cond.eval(scope))) {
            trueBranch.execute(scope);
        } else {
            falseBranch.execute(scope);
        }
    }
}
