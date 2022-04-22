package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

/**
 * 变量引用
 */
public class Var implements Expr {
    private final String varName;

    public Var(String varName) {
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public Value eval(Scope scope) {
        return scope.getVar(varName);
    }
}
