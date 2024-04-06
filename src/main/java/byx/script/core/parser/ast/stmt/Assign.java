package byx.script.core.parser.ast.stmt;

import byx.script.core.parser.ast.expr.Expr;

/**
 * 赋值语句
 */
public record Assign(Expr lhs, Expr rhs) implements Statement {}
