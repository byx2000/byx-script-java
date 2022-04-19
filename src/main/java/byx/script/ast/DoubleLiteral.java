package byx.script.ast;

import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public class DoubleLiteral implements Expr {
    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(value);
    }
}
