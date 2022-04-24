package byx.script.ast.expr;

import byx.script.runtime.Scope;
import byx.script.runtime.value.Value;

/**
 * 一元表达式
 */
public class UnaryExpr implements Expr {
    private final UnaryOp op;
    private final Expr e;

    public UnaryExpr(UnaryOp op, Expr e) {
        this.op = op;
        this.e = e;
    }

    @Override
    public Value eval(Scope scope) {
        return switch (op) {
            case Not -> e.eval(scope).not();
            case Neg -> e.eval(scope).neg();
        };
    }
}
