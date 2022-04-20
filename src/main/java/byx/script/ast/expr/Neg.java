package byx.script.ast.expr;

import byx.script.runtime.value.Value;

public class Neg extends UnaryOp {
    public Neg(Expr expr) {
        super(expr);
    }

    @Override
    protected Value doEval(Value v) {
        return v.neg();
    }
}
