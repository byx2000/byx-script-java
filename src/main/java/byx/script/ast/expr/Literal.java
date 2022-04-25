package byx.script.ast.expr;

import byx.script.ast.ASTVisitor;
import byx.script.runtime.value.Value;

/**
 * 字面量
 */
public class Literal implements Expr {
    private final Value value;

    public Literal(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
