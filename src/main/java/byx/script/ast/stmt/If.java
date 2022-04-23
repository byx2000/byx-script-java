package byx.script.ast.stmt;

import byx.script.ast.expr.Expr;
import byx.script.parserc.Pair;
import byx.script.runtime.Scope;
import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.value.BoolValue;
import byx.script.runtime.value.Value;

import java.util.List;

public class If implements Statement {
    private final List<Pair<Expr, Statement>> cases;
    private final Statement elseBranch;

    public If(List<Pair<Expr, Statement>> cases, Statement elseBranch) {
        this.cases = cases;
        this.elseBranch = elseBranch;
    }

    private boolean getCondition(Value v) {
        if (v instanceof BoolValue) {
            return ((BoolValue) v).getValue();
        }
        throw new InterpretException("condition of if statement must be bool value");
    }

    @Override
    public void execute(Scope scope) {
        for (Pair<Expr, Statement> p : cases) {
            if (getCondition(p.getFirst().eval(scope))) {
                p.getSecond().execute(scope);
                return;
            }
        }
        elseBranch.execute(scope);
    }
}
