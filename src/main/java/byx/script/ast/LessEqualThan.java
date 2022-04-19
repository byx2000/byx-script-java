package byx.script.ast;

import byx.script.runtime.Value;

public class LessEqualThan extends BinaryOp {
    public LessEqualThan(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return v1.lessEqualThan(v2);
    }
}
