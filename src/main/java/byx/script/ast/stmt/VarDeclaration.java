package byx.script.ast.stmt;

import byx.script.ast.ASTVisitor;
import byx.script.ast.expr.Expr;

/**
 * 变量声明
 * var varName = expr
 */
public class VarDeclaration implements Statement {
    private final String varName;
    private final Expr value;

    public VarDeclaration(String varName, Expr value) {
        this.varName = varName;
        this.value = value;
    }

    public String getVarName() {
        return varName;
    }

    public Expr getValue() {
        return value;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(ctx, this);
    }
}
