package byx.script.core.parser.ast;

import byx.script.core.interpreter.ASTVisitor;
import byx.script.core.parser.ast.stmt.Statement;

import java.util.List;

/**
 * 封装解析后的程序
 */
public class Program extends ASTNode {
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
    protected  <R, C> R doVisit(ASTVisitor<R, C> visitor, C ctx) {
        return visitor.visit(this, ctx);
    }
}
