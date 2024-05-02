package byx.script.core.parser.ast.stmt;

import byx.script.core.parser.ast.expr.Expr;

/**
 * if语句
 * if (expr) stmt
 * else if (expr) stmt
 * ...
 * else stmt
 */
public record If(Expr cond, Statement trueBody, Statement falseBody) implements Statement {}
