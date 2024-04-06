package byx.script.core.parser.ast.stmt;

import byx.script.core.interpreter.ASTVisitor;

import java.util.List;

/**
 * 语句块
 */
public class Block extends Statement {
    private final List<Statement> stmts;

    public Block(List<Statement> stmts) {
        this.stmts = stmts;
    }

    public List<Statement> getStmts() {
        return stmts;
    }

    @Override
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
