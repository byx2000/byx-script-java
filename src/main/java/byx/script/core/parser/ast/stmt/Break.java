package byx.script.core.parser.ast.stmt;

import byx.script.core.interpreter.ASTVisitor;

/**
 * 跳出循环
 */
public class Break extends Statement {
    public static final Break INSTANCE = new Break();

    private Break() {}

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
