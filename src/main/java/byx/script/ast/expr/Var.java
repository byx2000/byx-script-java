package byx.script.ast.expr;

import byx.script.ast.ASTVisitor;

/**
 * 变量引用
 */
public class Var implements Expr {
    private final String varName;

    public Var(String varName) {
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(ctx, this);
    }
}
