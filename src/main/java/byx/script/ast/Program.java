package byx.script.ast;

import byx.script.ast.stmt.Statement;

import java.util.List;

/**
 * 封装解析后的程序
 */
public class Program implements ASTNode {
    private final List<String> imports;
    private final List<Statement> stmts;

    public Program(List<String> imports, List<Statement> stmts) {
        this.imports = imports;
        this.stmts = stmts;
    }

    public List<String> getImports() {
        return imports;
    }

    public List<Statement> getStmts() {
        return stmts;
    }

    @Override
    public <R, C> R visit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
