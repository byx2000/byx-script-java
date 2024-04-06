package byx.script.core.parser.ast.stmt;

import byx.script.core.interpreter.ASTVisitor;

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
public class Try extends Statement {
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
    protected <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
