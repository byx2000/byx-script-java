package byx.script.ast;

import byx.script.runtime.Value;

public class Neg extends UnaryOp {
    public Neg(Expr expr) {
        super(expr);
    }

    @Override
    protected Value doEval(Value v) {
        return v.neg();
    }
}
