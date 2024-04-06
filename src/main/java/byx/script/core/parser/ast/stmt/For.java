package byx.script.core.parser.ast.stmt;

import byx.script.core.parser.ast.expr.Expr;

/**
 * for语句
 * for (init; cond; update)
 *     body
 */
public record For(Statement init, Expr cond, Statement update, Statement body) implements Statement {}
