package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.BoolValue;
import byx.script.runtime.value.Value;

/**
 * 二元表达式
 */
public class BinaryExpr implements Expr {
    private final BinaryOp op;
    private final Expr lhs, rhs;

    public BinaryExpr(BinaryOp op, Expr lhs, Expr rhs) {
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    private Value evalAnd(Scope scope) {
        Value v1 = lhs.eval(scope);
        // 实现短路特性
        if (v1 instanceof BoolValue && !((BoolValue) v1).getValue()) {
            return Value.of(false);
        }
        return v1.and(rhs.eval(scope));
    }

    private Value evalOr(Scope scope) {
        Value v2 = lhs.eval(scope);
        // 实现短路特性
        if (v2 instanceof BoolValue && ((BoolValue) v2).getValue()) {
            return Value.of(true);
        }
        return v2.or(rhs.eval(scope));
    }

    @Override
    public Value eval(Scope scope) {
        return switch (op) {
            case Add -> lhs.eval(scope).add(rhs.eval(scope));
            case Sub -> lhs.eval(scope).sub(rhs.eval(scope));
            case Mul -> lhs.eval(scope).mul(rhs.eval(scope));
            case Div -> lhs.eval(scope).div(rhs.eval(scope));
            case Rem -> lhs.eval(scope).rem(rhs.eval(scope));
            case LessThan -> lhs.eval(scope).lessThan(rhs.eval(scope));
            case LessEqualThan -> lhs.eval(scope).lessEqualThan(rhs.eval(scope));
            case GreaterThan -> lhs.eval(scope).greaterThan(rhs.eval(scope));
            case GreaterEqualThan -> lhs.eval(scope).greaterEqualThan(rhs.eval(scope));
            case Equal -> lhs.eval(scope).equal(rhs.eval(scope));
            case NotEqual -> lhs.eval(scope).notEqual(rhs.eval(scope));
            case And -> evalAnd(scope);
            case Or -> evalOr(scope);
        };
    }
}
