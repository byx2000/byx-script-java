package byx.script.ast.stmt;

import byx.script.ast.expr.Expr;
import byx.script.runtime.*;
import byx.script.runtime.control.BreakException;
import byx.script.runtime.control.ContinueException;
import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.value.BoolValue;
import byx.script.runtime.value.Value;

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

    private boolean getCondition(Value v) {
        if (v instanceof BoolValue) {
            return ((BoolValue) v).getValue();
        }
        throw new InterpretException("condition of for statement must be bool value");
    }

    @Override
    public void execute(Scope scope) {
        scope = new Scope(scope);
        for (init.execute(scope); getCondition(cond.eval(scope)); update.execute(scope)) {
            try {
                body.execute(scope);
            } catch (BreakException e) {
                break;
            } catch (ContinueException ignored) {}
        }
    }
}
