package byx.script.ast;

import byx.script.runtime.Scope;
import byx.script.runtime.Value;

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
