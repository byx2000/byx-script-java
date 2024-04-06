package byx.script.core.parser.ast.stmt;

import java.util.List;

/**
 * 语句块
 */
public record Block(List<Statement> stmts) implements Statement {}
