package byx.script.core.parser.ast.stmt;

import byx.script.core.common.Pair;
import byx.script.core.parser.ast.expr.Expr;

import java.util.List;

/**
 * if语句
 * if (expr) stmt
 * else if (expr) stmt
 * ...
 * else stmt
 */
public record If(List<Pair<Expr, Statement>> cases, Statement elseBranch) implements Statement {}
