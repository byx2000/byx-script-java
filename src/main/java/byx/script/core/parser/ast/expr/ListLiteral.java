package byx.script.core.parser.ast.expr;

import byx.script.core.interpreter.ASTVisitor;

import java.util.List;

/**
 * 列表字面量
 */
public class ListLiteral extends Expr {
    private final List<Expr> elems;

    public ListLiteral(List<Expr> elems) {
        this.elems = elems;
    }

    public List<Expr> getElems() {
        return elems;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
