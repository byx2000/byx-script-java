package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.value.BoolValue;
import byx.script.runtime.Scope;
import byx.script.runtime.Value;

/**
 * 与（&&）
 */
public class And implements Expr {
    private final Expr lhs, rhs;

    public And(Expr lhs, Expr rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public Value eval(Scope scope) {
        Value v1 = lhs.eval(scope);
        // 实现短路特性
        if (v1 instanceof BoolValue && !((BoolValue) v1).getValue()) {
            return Value.of(false);
        }
        return v1.and(rhs.eval(scope));
    }
}
