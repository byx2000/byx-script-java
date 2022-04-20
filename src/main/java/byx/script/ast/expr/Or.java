package byx.script.ast.expr;

import byx.script.runtime.value.BoolValue;
import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

/**
 * 或（||）
 */
public class Or implements Expr {
    private final Expr lhs, rhs;

    public Or(Expr lhs, Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Value eval(Scope scope) {
        Value v1 = lhs.eval(scope);
        // 实现短路特性
        if (v1 instanceof BoolValue && ((BoolValue) v1).getValue()) {
            return Value.of(true);
        }
        return v1.or(rhs.eval(scope));
    }
}
