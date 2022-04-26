package byx.script.parser.ast.expr;

import byx.script.parser.ast.ASTVisitor;
import byx.script.interpreter.value.Value;

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
