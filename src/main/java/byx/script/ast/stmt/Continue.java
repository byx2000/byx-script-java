package byx.script.ast.stmt;

import byx.script.ast.ASTVisitor;

/**
 * 继续循环
 */
public class Continue implements Statement {
    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(ctx, this);
    }
}
