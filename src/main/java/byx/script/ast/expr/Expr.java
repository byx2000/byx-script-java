package byx.script.ast.expr;

import byx.script.runtime.exception.InterpretException;
import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

public interface Expr {
    Value eval(Scope scope);

    default void assign(Scope scope, Expr rhs) {
        throw new InterpretException("unsupported assign: " + this);
    }
}
