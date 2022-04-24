package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

/**
 * 字面量
 */
public class Literal implements Expr {
    private final Value value;

    public Literal(Value value) {
        this.value = value;
    }

    @Override
    public Value eval(Scope scope) {
        return value;
    }
}
