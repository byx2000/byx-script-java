package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.Value;

/**
 * 加（+）
 */
public class Add extends BinaryOp {
    public Add(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    @Override
    protected Value doEval(Value v1, Value v2) {
        return v1.add(v2);
    }
}
