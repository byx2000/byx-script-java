package byx.script.ast;

import byx.script.runtime.Value;

public class GreaterThan extends BinaryOp {
    public GreaterThan(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return v1.greaterThan(v2);
    }
}
