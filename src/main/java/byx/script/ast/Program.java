package byx.script.ast;

import byx.script.runtime.Scope;

import java.util.List;

/**
 * 封装解析后的程序
 */
public class Program {
    private final List<String> imports;
    private final List<Statement> stmts;

    public Program(List<String> imports, List<Statement> stmts) {
        this.imports = imports;
        this.stmts = stmts;
    }

    public List<String> getImports() {
        return imports;
    }

    public void run(Scope scope) {
        for (Statement s : stmts) {
            s.execute(scope);
        }
    }
}
