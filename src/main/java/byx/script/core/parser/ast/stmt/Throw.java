package byx.script.core.parser.ast.stmt;

import byx.script.core.parser.ast.expr.Expr;

/**
 * throw语句
 */
public record Throw(Expr expr) implements Statement {}
