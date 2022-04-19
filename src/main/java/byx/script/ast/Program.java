package byx.script.ast;

import byx.script.runtime.Scope;

import java.util.List;

public class Program {
    private final List<Statement> stmts;

    public Program(List<Statement> stmts) {
        this.stmts = stmts;
    }

    public void run() {
        Scope scope = new Scope();
        for (Statement s : stmts) {
            s.execute(scope);
        }
    }
}
