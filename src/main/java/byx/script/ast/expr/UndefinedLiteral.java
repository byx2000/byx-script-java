package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.Scope;
import byx.script.runtime.value.UndefinedValue;
import byx.script.runtime.Value;

public class UndefinedLiteral implements Expr {
    @Override
    public Value eval(Scope scope) {
        return UndefinedValue.INSTANCE;
    }
}
