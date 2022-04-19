package byx.script.ast;

import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public class IntegerLiteral implements Expr {
    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(value);
    }
}
