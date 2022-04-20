package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

public class Var implements Expr {
    private final String varName;

    public Var(String varName) {
        this.varName = varName;
    }

    @Override
    public Value eval(Scope scope) {
        return scope.getVar(varName);
    }

    @Override
    public void assign(Scope scope, Expr rhs) {
        scope.setVar(varName, rhs.eval(scope));
    }
}
