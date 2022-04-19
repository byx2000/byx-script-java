package byx.script.ast;

import byx.script.runtime.Scope;

import java.util.List;

public class Block implements Statement {
    private final List<Statement> stmts;

    public Block(List<Statement> stmts) {
        this.stmts = stmts;
    }

    @Override
    public void execute(Scope scope) {
        scope = new Scope(scope);
        for (Statement s : stmts) {
            s.execute(scope);
        }
    }
}
