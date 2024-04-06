package byx.script.core.parser.ast.stmt;

import byx.script.core.parser.ast.expr.Expr;

/**
 * while语句
 * while (cond)
 * body
 */
public record While(Expr cond, Statement body) implements Statement {}
