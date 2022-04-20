package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.Value;

public class Sub extends BinaryOp {
    public Sub(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return v1.sub(v2);
    }
}
