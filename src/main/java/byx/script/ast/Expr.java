package byx.script.ast;

import byx.script.runtime.InterpretException;
import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public interface Expr {
    Value eval(Scope scope);

    default void assign(Scope scope, Expr rhs) {
        throw new InterpretException("unsupported assign: " + this);
    }
}
