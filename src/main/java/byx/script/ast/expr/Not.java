package byx.script.ast.expr;

import byx.script.ast.Expr;
import byx.script.runtime.Value;

/**
 * 非（!）
 */
public class Not extends UnaryOp {
    public Not(Expr expr) {
        super(expr);
    }

    @Override
    protected Value doEval(Value v) {
        return v.not();
    }
}
