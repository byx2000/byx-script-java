package byx.script.ast;

import byx.script.runtime.Value;

public class NotEqual extends BinaryOp {
    public NotEqual(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return v1.notEqual(v2);
    }
}
