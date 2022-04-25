package byx.script.ast.stmt;

import byx.script.ast.ASTVisitor;

import java.util.List;

/**
 * 语句块
 */
public class Block implements Statement {
    private final List<Statement> stmts;

    public Block(List<Statement> stmts) {
        this.stmts = stmts;
    }

    public List<Statement> getStmts() {
        return stmts;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
