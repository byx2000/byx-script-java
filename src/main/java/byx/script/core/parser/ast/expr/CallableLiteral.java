package byx.script.core.parser.ast.expr;

import byx.script.core.parser.ast.stmt.Statement;

import java.util.List;

/**
 * 函数字面量
 */
public record CallableLiteral(List<String> params, List<Statement> body) implements Expr {}
