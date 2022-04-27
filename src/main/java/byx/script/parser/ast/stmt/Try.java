package byx.script.parser.ast.stmt;

import byx.script.parser.ast.ASTVisitor;

/**
 * try-catch-finally语句
 * try {
 *     stmts
 * } catch {
 *     stmts
 * } finally {
 *     stmts
 * }
 */
public class Try implements Statement {
    private final Statement tryBranch;
    private final String catchVar;
    private final Statement catchBranch;
    private final Statement finallyBranch;

    public Try(Statement tryBranch, String catchVar, Statement catchBranch, Statement finallyBranch) {
        this.tryBranch = tryBranch;
        this.catchVar = catchVar;
        this.catchBranch = catchBranch;
        this.finallyBranch = finallyBranch;
    }

    public Statement getTryBranch() {
        return tryBranch;
    }

    public String getCatchVar() {
        return catchVar;
    }

    public Statement getCatchBranch() {
        return catchBranch;
    }

    public Statement getFinallyBranch() {
        return finallyBranch;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
