package byx.script.core.parser.ast;

import byx.script.core.parser.ast.stmt.Statement;

import java.util.List;

/**
 * 封装解析后的程序
 */
public record Program(List<String> imports, List<Statement> stmts) implements ASTNode {}
