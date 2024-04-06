package byx.script.core.parser.ast.expr;

import byx.script.core.interpreter.value.Value;
import byx.script.core.interpreter.ASTVisitor;

/**
 * 字面量
 */
public class Literal extends Expr {
    private final Value value;

    public Literal(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
