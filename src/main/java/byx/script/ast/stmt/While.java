package byx.script.ast.stmt;

import byx.script.ast.expr.Expr;
import byx.script.runtime.*;
import byx.script.runtime.control.BreakException;
import byx.script.runtime.control.ContinueException;
import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.value.BoolValue;
import byx.script.runtime.value.Value;

public class While implements Statement {
    private final Expr cond;
    private final Statement body;

    public While(Expr cond, Statement body) {
        this.cond = cond;
        this.body = body;
    }

    private boolean getCondition(Value v) {
        if (v instanceof BoolValue) {
            return ((BoolValue) v).getValue();
        }
        throw new InterpretException("condition of while statement must be bool value");
    }

    @Override
    public void execute(Scope scope) {
        while (getCondition(cond.eval(scope))) {
            try {
                body.execute(scope);
            } catch (BreakException e) {
                break;
            } catch (ContinueException ignored) {}
        }
    }
}
