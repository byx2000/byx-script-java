package byx.script.ast;

import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public class StringLiteral implements Expr {
    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public Value eval(Scope scope) {
        return Value.of(value);
    }
}
