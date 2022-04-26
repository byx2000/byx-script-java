package byx.script.parser.ast.expr;

import byx.script.parser.ast.ASTVisitor;

import java.util.List;

/**
 * 列表字面量
 */
public class ListLiteral implements Expr {
    private final List<Expr> elems;

    public ListLiteral(List<Expr> elems) {
        this.elems = elems;
    }

    public List<Expr> getElems() {
        return elems;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
