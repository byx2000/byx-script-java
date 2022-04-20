package byx.script.ast.expr;

import byx.script.runtime.value.Value;

public class LessThan extends BinaryOp {
    public LessThan(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return v1.lessThan(v2);
    }
}
