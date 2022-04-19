package byx.script.ast;

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
