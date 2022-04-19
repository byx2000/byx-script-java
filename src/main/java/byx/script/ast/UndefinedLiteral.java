package byx.script.ast;

import byx.script.runtime.Scope;
import byx.script.runtime.Value;

public class UndefinedLiteral implements Expr {
    @Override
    public Value eval(Scope scope) {
        return Value.UNDEFINED;
    }
}
