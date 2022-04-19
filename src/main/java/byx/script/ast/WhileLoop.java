package byx.script.ast;

import byx.script.runtime.BreakException;
import byx.script.runtime.ContinueException;
import byx.script.runtime.Scope;

public class WhileLoop implements Statement {
    private final Expr cond;
    private final Statement body;

    public WhileLoop(Expr cond, Statement body) {
        this.cond = cond;
        this.body = body;
    }

    @Override
    public void execute(Scope scope) {
        while (cond.eval(scope).toCondition()) {
            try {
                body.execute(scope);
            } catch (BreakException e) {
                break;
            } catch (ContinueException ignored) {}
        }
    }
}
