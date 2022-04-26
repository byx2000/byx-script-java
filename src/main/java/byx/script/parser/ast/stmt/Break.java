package byx.script.parser.ast.stmt;

import byx.script.parser.ast.ASTVisitor;

/**
 * 跳出循环
 */
public class Break implements Statement {
    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
