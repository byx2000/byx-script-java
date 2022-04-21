package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

public class BoolLiteral implements Expr {
    private final boolean value;

    public BoolLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(value);
    }
}